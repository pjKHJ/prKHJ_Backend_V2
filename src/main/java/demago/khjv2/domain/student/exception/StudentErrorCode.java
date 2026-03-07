package demago.khjv2.domain.student.exception;

import demago.khjv2.global.error.ErrorCode;
import org.springframework.http.HttpStatus;

// 학생 도메인 전용 에러 코드
// 일단 만들긴 했는데 좀더 코드 검증도 해보고 다듬어야 할듯
public enum StudentErrorCode implements ErrorCode {

    // ==================== 400 Bad Request ====================
    INVALID_CREATE_REQUEST(HttpStatus.BAD_REQUEST, "STD_400_01", "잘못된 학생 등록 요청입니다."),
    INVALID_BULK_REQUEST(HttpStatus.BAD_REQUEST, "STD_400_02", "잘못된 일괄 학생 등록 요청입니다."),
    INVALID_DELETE_REQUEST(HttpStatus.BAD_REQUEST, "STD_400_03", "잘못된 학생 삭제 요청입니다."),

    INVALID_STUDENT_NUMBER(HttpStatus.BAD_REQUEST, "STD_400_04", "학번은 1부터 9999 사이의 숫자여야 합니다."),
    INVALID_BOJ_ID(HttpStatus.BAD_REQUEST, "STD_400_05", "BOJ ID는 1~50자 사이의 문자열이어야 합니다."),
    INVALID_STUDENT_NAME(HttpStatus.BAD_REQUEST, "STD_400_06", "학생 이름은 1~10자 사이여야 합니다."),

    BULK_CREATE_FAILED(HttpStatus.BAD_REQUEST, "STD_400_07", "등록 가능한 학생 데이터가 없습니다."),

    // ==================== 404 Not Found ====================
    STUDENT_NOT_FOUND(HttpStatus.NOT_FOUND, "STD_404", "학생을 찾을 수 없습니다."),

    // ==================== 409 Conflict ====================
    STUDENT_CONFLICT(HttpStatus.CONFLICT, "STD_409", "이미 존재하는 학번 또는 BOJ ID입니다."),

    // ==================== 500 Internal Server Error ====================
    STUDENT_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "STD_500_01", "학생 생성 중 오류가 발생했습니다."),
    STUDENT_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "STD_500_02", "학생 삭제 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    StudentErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}