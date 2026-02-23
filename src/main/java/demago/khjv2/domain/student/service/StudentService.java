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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 학생(관리자) 등록/삭제 기능을 담당하는 서비스.
 *
 * <p>주의: STUDENT_DETAIL(1:1), STUDENT_GRASS(1:N) 등 연관관계는 FK 무결성/NPE 이슈로 인해
 * Student 엔티티에서 비활성화(주석 처리)되어 있으며, 본 서비스는 해당 관계를 절대 건드리지 않는다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    /**
     * DB(MEDIUMINT) 상 최대 범위는 32767이지만,
     * 요구사항에 따라 학번 현실성 검증을 위해 9999까지로 제한한다.
     */
    private static final int STUDENT_NO_MIN = 1;
    private static final int STUDENT_NO_MAX_POLICY = 9999;

    private final StudentRepository studentRepository;
    private final StudentWriteService studentWriteService;

    /**
     * 단건 학생 등록.
     *
     * @param request 학생 등록 요청 DTO
     * @return 등록된 학생 정보
     */
    @Transactional
    public StudentResponse createStudent(final StudentCreateRequest request) {
        if (request == null) {
            // 컨트롤러 단에서 @Valid로 대부분 걸러지지만, 서비스 레벨에서도 방어한다.
            throw new KHJException(StudentErrorCode.INVALID_CREATE_REQUEST);
        }

        final String name = normalizeName(request.getName());
        final String bojId = normalizeBojId(request.getBojId());
        final Integer studentNo = normalizeStudentNumber(request.getStudentNumber());

        // 학번/BOJ ID 중 하나라도 이미 존재하면 충돌로 간주한다.
        if (studentRepository.existsByStudentNo(studentNo) || studentRepository.existsByBojId(bojId)) {
            throw new KHJException(StudentErrorCode.STUDENT_CONFLICT);
        }

        try {
            final Student student = Student.create(name, studentNo, bojId);
            final Student saved = studentRepository.save(student);
            return toResponse(saved);
        } catch (final DataIntegrityViolationException e) {
            final Throwable root = e.getMostSpecificCause();
            final String msg = (root != null ? root.getMessage() : e.getMessage());
            final String lower = (msg == null ? "" : msg.toLowerCase());

            final boolean looksLikeUnique =
                    lower.contains("duplicate")
                            || lower.contains("uk_student_student_no")
                            || lower.contains("uk_student_boj_id");

            if (looksLikeUnique) {
                throw new KHJException(StudentErrorCode.STUDENT_CONFLICT);
            }

            log.error("Student create failed due to data integrity violation.", e);
            throw new KHJException(StudentErrorCode.STUDENT_CREATE_FAILED);
        }
    }

    /**
     * 일괄 학생 등록.
     *
     * <p>정책: 부분 성공(Partial Success)</p>
     * <ul>
     *   <li>요청 내 중복(학번/BOJ ID)은 스킵</li>
     *   <li>DB에 이미 존재하는 학번/BOJ ID도 스킵</li>
     *   <li>그 외 유효성(학번 파싱/범위)은 필수 검증</li>
     * </ul>
     *
     * @param request 일괄 등록 요청 DTO
     * @return 생성된 학생 목록
     */
    // ✅ 벌크 메서드는 "오케스트레이션"만 하고, 저장은 REQUIRES_NEW로 건별 처리
    public StudentBulkCreateResponse createBulkStudents(final StudentBulkCreateRequest request) {
        if (request == null || request.students() == null || request.students().isEmpty()) {
            throw new KHJException(StudentErrorCode.INVALID_BULK_REQUEST);
        }

        final List<StudentCreateRequest> requests = request.students();

        // 요청 내 중복 제거(순서 유지)
        final Set<Integer> seenStudentNos = new LinkedHashSet<>();
        final Set<String> seenBojIds = new LinkedHashSet<>();

        final List<Student> toCreate = new ArrayList<>();
        for (final StudentCreateRequest r : requests) {
            if (r == null) {
                // @Valid로 대부분 걸러지지만, null 요소가 들어오면 스킵한다.
                continue;
            }

            final String name = normalizeName(r.getName());
            final String bojId = normalizeBojId(r.getBojId());
            final Integer studentNo = normalizeStudentNumber(r.getStudentNumber());

            // DB 중복은 스킵(부분 성공) - 먼저 확인해서 "실패 데이터"가 Set을 오염시키지 않게 함
            if (studentRepository.existsByStudentNo(studentNo) || studentRepository.existsByBojId(bojId)) {
                continue;
            }

            // 요청 내 중복은 스킵(부분 성공) - 생성 후보만 중복 제거 대상으로 삼음
            if (!seenStudentNos.add(studentNo)) {
                continue;
            }
            if (!seenBojIds.add(bojId)) {
                continue;
            }

            toCreate.add(Student.create(name, studentNo, bojId));
        }

        if (toCreate.isEmpty()) {
            // 생성 가능한 데이터가 하나도 없으면 실패로 처리(요구사항의 BULK_CREATE_FAILED 활용)
            throw new KHJException(StudentErrorCode.BULK_CREATE_FAILED);
        }

        final List<StudentResponse> created = new ArrayList<>();

        for (final Student student : toCreate) {
            try {
                final Student saved = studentWriteService.saveAndFlush(student); // REQUIRES_NEW
                created.add(toResponse(saved));
            } catch (final DataIntegrityViolationException e) {
                // 부분 성공 정책: 유니크 충돌/레이스 등으로 실패한 건만 스킵
                log.debug("Skip one student in bulk create due to integrity violation.");
            }
        }

        if (created.isEmpty()) {
            throw new KHJException(StudentErrorCode.BULK_CREATE_FAILED);
        }

        return new StudentBulkCreateResponse(List.copyOf(created));
    }

    /**
     * 학생 삭제(IDs 기반).
     *
     * <p>정책: 실제 삭제 여부(존재/미존재)는 확인하지 않고 요청 ids를 그대로 반환한다.</p>
     *
     * @param request 삭제 요청 DTO
     * @return 삭제 요청된 ids
     */
    @Transactional
    public StudentDeleteResponse deleteStudents(final StudentDeleteRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            throw new KHJException(StudentErrorCode.INVALID_DELETE_REQUEST);
        }

        final List<Long> ids = request.getIds();
        for (final Long id : ids) {
            if (id == null) {
                throw new KHJException(StudentErrorCode.INVALID_DELETE_REQUEST);
            }
        }

        try {
            studentRepository.deleteAllByIdInBatch(ids);
            return new StudentDeleteResponse(List.copyOf(ids));
        } catch (final DataIntegrityViolationException e) {
            // FK 무결성 등으로 삭제가 실패할 수 있다.
            throw new KHJException(StudentErrorCode.STUDENT_DELETE_FAILED);
        }
    }

    private StudentResponse toResponse(final Student student) {
        // Student 엔티티의 필드는 @PrePersist에서 trim 되지만, 응답은 엔티티 값을 그대로 사용한다.
        return new StudentResponse(
                student.getId(),
                String.valueOf(student.getStudentNo()),
                student.getName(),
                student.getBojId()
        );
    }

    private static final int NAME_MAX_LEN = 10;
    private static final int BOJ_ID_MAX_LEN = 50;
    private static String normalizeName(final String value) {
        if (!StringUtils.hasText(value)) {
            throw new KHJException(StudentErrorCode.INVALID_STUDENT_NAME);
        }
        final String trimmed = value.trim();
        if (trimmed.length() > NAME_MAX_LEN) {
            throw new KHJException(StudentErrorCode.INVALID_STUDENT_NAME);
        }
        return trimmed;
    }
    private static String normalizeBojId(final String value) {
        if (!StringUtils.hasText(value)) {
            throw new KHJException(StudentErrorCode.INVALID_BOJ_ID);
        }
        final String trimmed = value.trim();
        if (trimmed.length() > BOJ_ID_MAX_LEN) {
            throw new KHJException(StudentErrorCode.INVALID_BOJ_ID);
        }
        return trimmed;
    }

    /**
     * 학번 문자열을 정규화(숫자 파싱 후 canonical string으로 반환)하고 범위를 검증한다.
     */
    private static Integer normalizeStudentNumber(final String studentNumberRaw) {
        if (!StringUtils.hasText(studentNumberRaw)) {
            throw new KHJException(StudentErrorCode.INVALID_STUDENT_NUMBER);
        }

        final String trimmed = studentNumberRaw.trim();
        final int parsed;
        try {
            parsed = Integer.parseInt(trimmed);
        } catch (final NumberFormatException e) {
            throw new KHJException(StudentErrorCode.INVALID_STUDENT_NUMBER);
        }

        // DB 최대치(32767)는 참고값, 실제 정책은 9999까지로 제한
        if (parsed < STUDENT_NO_MIN || parsed > STUDENT_NO_MAX_POLICY) {
            throw new KHJException(StudentErrorCode.INVALID_STUDENT_NUMBER);
        }

        // "0001" 같은 입력을 "1"로 정규화하여 UNIQUE 충돌을 일관되게 처리한다.
        return parsed;
    }
}