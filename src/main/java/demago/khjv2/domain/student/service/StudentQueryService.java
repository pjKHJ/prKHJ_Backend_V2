package demago.khjv2.domain.student.service;

import demago.khjv2.domain.student.entity.Student;
import demago.khjv2.domain.student.exception.StudentErrorCode;
import demago.khjv2.domain.student.presentation.dto.response.ManagementStudentListResponse;
import demago.khjv2.domain.student.presentation.dto.response.StudentResponse;
import demago.khjv2.domain.student.presentation.dto.response.data.ClassCompetitionItemResponse;
import demago.khjv2.domain.student.presentation.dto.response.data.ClassCompetitionResponse;
import demago.khjv2.domain.student.presentation.dto.response.data.StudentDetailResponse;
import demago.khjv2.domain.student.presentation.dto.response.data.StudentGrassItemResponse;
import demago.khjv2.domain.student.presentation.dto.response.data.StudentGrassResponse;
import demago.khjv2.domain.student.presentation.dto.response.data.StudentListItemResponse;
import demago.khjv2.domain.student.presentation.dto.response.data.StudentListResponse;
import demago.khjv2.domain.student.repository.StudentRepository;
import demago.khjv2.domain.studentDetail.entity.StudentDetails;
import demago.khjv2.domain.studentDetail.repository.StudentDetailsRepository;
import demago.khjv2.domain.studentGrass.entity.BojGrass;
import demago.khjv2.domain.studentGrass.entity.BojGrassHistory;
import demago.khjv2.domain.studentGrass.repository.BojGrassHistoryRepository;
import demago.khjv2.global.error.exception.KHJException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentQueryService {

    private static final int DEFAULT_PERIOD = 30;
    private static final int MIN_PERIOD = 1;
    private static final int MAX_PERIOD = 365;

    private final StudentRepository studentRepository;
    private final StudentDetailsRepository studentDetailsRepository;
    private final BojGrassHistoryRepository bojGrassHistoryRepository;

    public StudentDetailResponse getStudentDetail(Long id) {
        Student student = getStudentOrThrow(id);
        StudentMetrics metrics = loadMetrics(student);

        return new StudentDetailResponse(
                student.getId(),
                String.valueOf(student.getStudentNo()),
                student.getName(),
                student.getBojId(),
                metrics.tier(),
                metrics.totalSolved(),
                metrics.accuracyRate(),
                metrics.todaySolved(),
                metrics.streak(),
                metrics.maxStreak(),
                metrics.flame()
        );
    }

    public StudentGrassResponse getStudentGrass(Long id, Integer period) {
        Student student = getStudentOrThrow(id);
        int normalizedPeriod = normalizePeriod(period);

        LocalDate fromDate = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(normalizedPeriod - 1L);

        List<StudentGrassItemResponse> grass = bojGrassHistoryRepository.findByBojId(student.getBojId())
                .map(BojGrassHistory::getGrass)
                .stream()
                .flatMap(List::stream)
                .filter(entry -> !entry.getDate().isBefore(fromDate))
                .sorted(Comparator.comparing(BojGrass::getDate).reversed())
                .map(entry -> new StudentGrassItemResponse(entry.getDate(), entry.getValue()))
                .toList();

        return new StudentGrassResponse(grass);
    }

    public StudentListResponse getStudents() {
        List<Student> students = studentRepository.findAllByOrderByStudentNoAsc();
        Map<Long, StudentDetails> detailsMap = loadDetailsMap(students);

        List<StudentListItemResponse> responses = students.stream()
                .map(student -> toStudentListItem(student, StudentMetrics.from(detailsMap.get(student.getId()))))
                .toList();

        return new StudentListResponse(responses);
    }

    public ClassCompetitionResponse getClassCompetition() {
        List<Student> students = studentRepository.findAllByOrderByStudentNoAsc();
        Map<Long, StudentDetails> detailsMap = loadDetailsMap(students);

        List<ClassCompetitionItemResponse> classStats = students.stream()
                .collect(Collectors.groupingBy(
                        student -> student.getStudentNo() / 1000,
                        TreeMap::new,
                        Collectors.toList()
                ))
                .entrySet()
                .stream()
                .map(entry -> aggregateClass(entry.getKey(), entry.getValue(), detailsMap))
                .toList();

        return new ClassCompetitionResponse(classStats);
    }

    public ManagementStudentListResponse getManagementStudents() {
        List<StudentResponse> students = studentRepository.findAllByOrderByStudentNoAsc().stream()
                .map(this::toStudentResponse)
                .toList();

        return new ManagementStudentListResponse(students);
    }

    private Student getStudentOrThrow(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new KHJException(StudentErrorCode.STUDENT_NOT_FOUND));
    }

    private int normalizePeriod(Integer period) {
        int normalized = period == null ? DEFAULT_PERIOD : period;
        if (normalized < MIN_PERIOD || normalized > MAX_PERIOD) {
            throw new KHJException(StudentErrorCode.INVALID_GRASS_PERIOD);
        }
        return normalized;
    }

    private Map<Long, StudentDetails> loadDetailsMap(List<Student> students) {
        return studentDetailsRepository.findAll().stream()
                .collect(Collectors.toMap(
                        details -> details.getStudentId().getId(),
                        Function.identity(),
                        (left, right) -> right
                ));
    }

    private StudentListItemResponse toStudentListItem(Student student, StudentMetrics metrics) {
        return new StudentListItemResponse(
                student.getId(),
                String.valueOf(student.getStudentNo()),
                student.getName(),
                student.getBojId(),
                metrics.tier(),
                metrics.totalSolved(),
                metrics.accuracyRate(),
                metrics.todaySolved(),
                metrics.streak(),
                metrics.maxStreak()
        );
    }

    private StudentResponse toStudentResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                String.valueOf(student.getStudentNo()),
                student.getName(),
                student.getBojId()
        );
    }

    private StudentMetrics loadMetrics(Student student) {
        return studentDetailsRepository.findByStudentId(student)
                .map(StudentMetrics::from)
                .orElse(StudentMetrics.empty());
    }

    private ClassCompetitionItemResponse aggregateClass(
            int classNumber,
            List<Student> students,
            Map<Long, StudentDetails> detailsMap
    ) {
        List<StudentMetrics> metricsList = students.stream()
                .map(student -> StudentMetrics.from(detailsMap.get(student.getId())))
                .toList();

        int tierAverage = (int) Math.round(metricsList.stream().mapToInt(StudentMetrics::tier).average().orElse(0.0));
        double accuracyAverage = roundToOneDecimal(metricsList.stream().mapToDouble(StudentMetrics::accuracyRate).average().orElse(0.0));
        int totalSolved = metricsList.stream().mapToInt(StudentMetrics::totalSolved).sum();
        int todaySolved = metricsList.stream().mapToInt(StudentMetrics::todaySolved).sum();
        int streakAverage = (int) Math.round(metricsList.stream().mapToInt(StudentMetrics::streak).average().orElse(0.0));
        int maxStreak = metricsList.stream().mapToInt(StudentMetrics::maxStreak).max().orElse(0);

        return new ClassCompetitionItemResponse(
                classNumber,
                tierAverage,
                totalSolved,
                accuracyAverage,
                todaySolved,
                streakAverage,
                maxStreak
        );
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private record StudentMetrics(
            int tier,
            int totalSolved,
            double accuracyRate,
            int todaySolved,
            int streak,
            int maxStreak,
            int flame
    ) {
        private static StudentMetrics from(StudentDetails details) {
            if (details == null) {
                return empty();
            }
            return new StudentMetrics(
                    details.getTier(),
                    details.getSolvedTotal(),
                    details.getAccuracyPct(),
                    details.getSolvedToday(),
                    details.getCurrentStreak(),
                    details.getLongestStreak(),
                    details.getFlame()
            );
        }

        private static StudentMetrics empty() {
            return new StudentMetrics(0, 0, 0.0, 0, 0, 0, 0);
        }
    }
}
