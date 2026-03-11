package demago.khjv2.domain.studentDetail.service;

import demago.khjv2.domain.student.entity.Student;
import demago.khjv2.domain.student.repository.StudentRepository;
import demago.khjv2.domain.studentDetail.entity.StudentDetails;
import demago.khjv2.domain.studentDetail.repository.StudentDetailsRepository;
import demago.khjv2.global.feignclient.boj.BojClient;
import demago.khjv2.global.feignclient.boj.BojDetailsResponse;
import demago.khjv2.global.feignclient.boj.BojGrassResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentDetailsSyncService {

    private static final int FLAME_MAX = 7;

    private final StudentRepository studentRepository;
    private final StudentDetailsRepository studentDetailsRepository;
    private final BojClient bojClient;

    public void syncUserDetail() {
        List<Student> studentList = studentRepository.findAll();

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        for (Student student : studentList) {
            StudentDetails studentDetails = studentDetailsRepository.findByStudentId(student)
                    .orElseGet(() -> StudentDetails.empty(student));

            BojGrassResponse bojGrassResponse = bojClient.getGrass(student.getBojId(), "default");
            BojDetailsResponse bojDetailsResponse = bojClient.getDetails(student.getBojId());

            int solvedToday = bojGrassResponse.grass().stream()
                    .filter(grass -> grass.date().equals(today))
                    .mapToInt(grass -> grass.value())
                    .findFirst()
                    .orElse(0);

            int accuracyPct = 0;

            studentDetails.applyFromApi(
                    bojDetailsResponse.tier(),
                    bojDetailsResponse.solvedCount(),
                    solvedToday,
                    accuracyPct,
                    bojGrassResponse.currentStreak(),
                    bojDetailsResponse.maxStreak(),
                    studentDetails.getFlame()
            );

            studentDetailsRepository.save(studentDetails);
        }
    }

    public void flame() {
        List<Student> studentList = studentRepository.findAll();

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        LocalDate targetDate = today;

        for (Student student : studentList) {
            StudentDetails studentDetails = studentDetailsRepository.findByStudentId(student)
                    .orElseGet(() -> StudentDetails.empty(student));

            BojGrassResponse bojGrassResponse = bojClient.getGrass(student.getBojId(), "default");
            BojDetailsResponse bojDetailsResponse = bojClient.getDetails(student.getBojId());

            int solvedToday = bojGrassResponse.grass().stream()
                    .filter(grass -> grass.date().equals(today))
                    .mapToInt(grass -> grass.value())
                    .findFirst()
                    .orElse(0);

            int accuracyPct = 0;

            int newFlame = calculateFlame(studentDetails.getFlame(), bojGrassResponse, targetDate);

            studentDetails.applyFromApi(
                    bojDetailsResponse.tier(),
                    bojDetailsResponse.solvedCount(),
                    solvedToday,
                    accuracyPct,
                    bojGrassResponse.currentStreak(),
                    bojDetailsResponse.maxStreak(),
                    newFlame
            );

            studentDetailsRepository.save(studentDetails);
        }
    }

    private int calculateFlame(int currentFlame, BojGrassResponse bojGrassResponse, LocalDate targetDate) {
        Map<LocalDate, Integer> solvedMap = bojGrassResponse.grass().stream()
                .collect(Collectors.toMap(
                        grass -> grass.date(),
                        grass -> grass.value(),
                        Integer::sum
                ));

        int solvedCount = solvedMap.getOrDefault(targetDate, 0);

        // 푼 날
        if (solvedCount > 0) {
            int reward = isWeekend(targetDate) ? 2 : 1;
            return Math.min(FLAME_MAX, currentFlame + reward);
        }

        // 안 푼 날
        int missedDays = countConsecutiveMissedDays(solvedMap, targetDate);

        int penalty = switch (missedDays) {
            case 1 -> 1;
            case 2 -> 2;
            default -> 4; // 3일 이상
        };

        return Math.max(0, currentFlame - penalty);
    }

    private int countConsecutiveMissedDays(Map<LocalDate, Integer> solvedMap, LocalDate targetDate) {
        int missedDays = 0;
        LocalDate date = targetDate;

        while (missedDays < 3) {
            int solvedCount = solvedMap.getOrDefault(date, 0);

            if (solvedCount > 0) {
                break;
            }

            missedDays++;
            date = date.minusDays(1);
        }

        return missedDays;
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
}