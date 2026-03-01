package demago.khjv2.domain.student.service;

import demago.khjv2.domain.student.entity.Student;
import demago.khjv2.domain.student.exception.StudentErrorCode;
import demago.khjv2.domain.student.presentation.dto.request.StudentBulkCreateRequest;
import demago.khjv2.domain.student.presentation.dto.request.StudentCreateRequest;
import demago.khjv2.domain.student.presentation.dto.request.StudentDeleteRequest;
import demago.khjv2.domain.student.presentation.dto.response.StudentBulkCreateResponse;
import demago.khjv2.domain.student.presentation.dto.response.StudentDeleteResponse;
import demago.khjv2.domain.student.presentation.dto.response.StudentResponse;
import demago.khjv2.domain.student.repository.StudentRepository;
import demago.khjv2.global.error.exception.KHJException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

// 학생 관리 서비스 (등록/삭제)

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    //학번 제한
    private static final int STUDENT_NO_MIN = 1000;
    private static final int STUDENT_NO_MAX_POLICY = 3999;

    private final StudentRepository studentRepository;
    private final StudentWriteService studentWriteService;


    //단건 학생 등록.
    @Transactional
    public StudentResponse createStudent(StudentCreateRequest request) {
        if (request == null) {
            throw new KHJException(StudentErrorCode.INVALID_CREATE_REQUEST);
        }

        final String name = normalizeName(request.name());
        final String bojId = normalizeBojId(request.bojId());
        final Integer studentNo = normalizeStudentNumber(request.studentNumber());

        // 학번/백준 ID 중 하나라도 이미 존재하면 충돌로 간주한다.
        if (studentRepository.existsByStudentNo(studentNo) || studentRepository.existsByBojId(bojId)) {
            throw new KHJException(StudentErrorCode.STUDENT_CONFLICT);
        }

        try {
            final Student student = Student.create(name, studentNo, bojId);
            final Student saved = studentRepository.save(student);
            return toResponse(saved);
        } catch (final DataIntegrityViolationException e) {
            // 유니크 키 충돌은 CONFLICT, 그 외는 CREATE_FAILED
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate")) {
                throw new KHJException(StudentErrorCode.STUDENT_CONFLICT);
            }
            throw new KHJException(StudentErrorCode.STUDENT_CREATE_FAILED);
        }
    }

    // 일괄 등록 중복은 모두 스킵함
    public StudentBulkCreateResponse createBulkStudents(StudentBulkCreateRequest request) {
        if (request == null || request.students() == null || request.students().isEmpty()) {
            throw new KHJException(StudentErrorCode.INVALID_BULK_REQUEST);
        }

        final List<StudentCreateRequest> requests = request.students();
        final Set<Integer> studentNos = new HashSet<>();
        final Set<String> bojIds = new HashSet<>();

        final List<Student> toCreate = new ArrayList<>();
        for (final StudentCreateRequest r : requests) {
            if (r == null) continue;

            final String name = normalizeName(r.name());
            final String bojId = normalizeBojId(r.bojId());
            final Integer studentNo = normalizeStudentNumber(r.studentNumber());

            // 이미 DB에 있거나 요청 안에서 중복이면 스킵
            if (studentRepository.existsByStudentNo(studentNo) || studentRepository.existsByBojId(bojId)) {
                continue;
            }
            if (!studentNos.add(studentNo) || !bojIds.add(bojId)) {
                continue;
            }

            toCreate.add(Student.create(name, studentNo, bojId));
        }

        if (toCreate.isEmpty()) {
            throw new KHJException(StudentErrorCode.BULK_CREATE_FAILED);
        }

        final List<StudentResponse> created = new ArrayList<>();
        for (final Student student : toCreate) {
            try {
                final Student saved = studentWriteService.saveAndFlush(student);
                created.add(toResponse(saved));
            } catch (final DataIntegrityViolationException e) {
                log.debug("벌크 등록 중 하나 스킵 (중복 충돌)", e);
            }
        }

        if (created.isEmpty()) {
            throw new KHJException(StudentErrorCode.BULK_CREATE_FAILED);
        }

        return new StudentBulkCreateResponse(List.copyOf(created));
    }

    // 학생 일괄 삭제
    @Transactional
    public StudentDeleteResponse deleteStudents(StudentDeleteRequest request) {
        if (request == null || request.ids() == null || request.ids().isEmpty()) {
            throw new KHJException(StudentErrorCode.INVALID_DELETE_REQUEST);
        }

        final List<Long> ids = request.ids();
        if (ids.contains(null)) {
            throw new KHJException(StudentErrorCode.INVALID_DELETE_REQUEST);
        }

        //상관 없다고 함
        // try {
        studentRepository.deleteAllByIdInBatch(ids);
        return new StudentDeleteResponse(List.copyOf(ids));
        // } catch (final DataIntegrityViolationException e) {
        //     throw new KHJException(StudentErrorCode.STUDENT_DELETE_FAILED);
        // }
    }

    // 엔티티를 응답 DTO로 바꿔주는 유틸 메서드
    private StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                String.valueOf(student.getStudentNo()),
                student.getName(),
                student.getBojId()
        );
    }

    // 이름/백준 id 최대 길이
    private static final int MAX_NAME_LENGTH = 10;
    private static final int MAX_BOJ_ID_LENGTH = 50;


    // 이름 정규화: 앞뒤 공백 제거 + 빈값/너무 긴 이름 막기 (최대 10자)
    private static String normalizeName(String value) {
        if (!StringUtils.hasText(value)) {
            throw new KHJException(StudentErrorCode.INVALID_STUDENT_NAME);
        }
        String trimmed = value.trim();
        if (trimmed.length() > MAX_NAME_LENGTH) {
            throw new KHJException(StudentErrorCode.INVALID_STUDENT_NAME);
        }
        return trimmed;
    }

    // 백준 id 정리: 앞뒤 공백 제거 + 빈값/너무 긴 ID(최대 50자) 막기
    private static String normalizeBojId(String value) {
        if (!StringUtils.hasText(value)) {
            throw new KHJException(StudentErrorCode.INVALID_BOJ_ID);
        }
        String trimmed = value.trim();
        if (trimmed.length() > MAX_BOJ_ID_LENGTH) {
            throw new KHJException(StudentErrorCode.INVALID_BOJ_ID);
        }
        return trimmed;
    }

    // 학번 입력값 정리: 문자열 → integer 변환 + 범위(1000~3999) 체크
    private static Integer normalizeStudentNumber(String studentNumberRaw) {
        if (!StringUtils.hasText(studentNumberRaw)) {
            throw new KHJException(StudentErrorCode.INVALID_STUDENT_NUMBER);
        }

        String trimmed = studentNumberRaw.trim();
        int num;
        try {
            num = Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            throw new KHJException(StudentErrorCode.INVALID_STUDENT_NUMBER);
        }

        if (num < STUDENT_NO_MIN || num > STUDENT_NO_MAX_POLICY) {
            throw new KHJException(StudentErrorCode.INVALID_STUDENT_NUMBER);
        }

        return num;
    }
}