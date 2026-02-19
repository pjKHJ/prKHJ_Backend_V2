package demago.khjv2.domain.user.exception;

import demago.khjv2.global.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserErrorCode implements ErrorCode {

    // 401
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "USR_401", "이름 또는 비밀번호가 올바르지 않습니다."),

    // 404
    USER_ERROR_CODE(HttpStatus.NOT_FOUND, "USR_404", "존재하지 않는 유저입니다."),

    // 409
    USERNAME_ERROR_CODE(HttpStatus.CONFLICT, "USR_409", "이미 가입된 이름입니다."),

    // 400
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "USR_400", "인증 코드가 올바르지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    UserErrorCode(HttpStatus status, String code, String message) {
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
