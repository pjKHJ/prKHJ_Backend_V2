package demago.khjv2.global.error;

import org.springframework.http.HttpStatus;

public enum GlobalErrorCode implements ErrorCode {

    // 400
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "GLB_400", "잘못된 요청입니다."),

    // 401/403
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "GLB_401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "GLB_403", "권한이 없습니다."),

    // 404
    NOT_FOUND(HttpStatus.NOT_FOUND, "GLB_404", "리소스를 찾을 수 없습니다."),

    // 409
    CONFLICT(HttpStatus.CONFLICT, "GLB_409", "요청이 충돌했습니다."),

    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GLB_500", "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    GlobalErrorCode(HttpStatus status, String code, String message) {
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
