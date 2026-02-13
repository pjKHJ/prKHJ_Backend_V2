package demago.khjv2.global.error.exception;

import demago.khjv2.global.error.ErrorCode;

// 서비스 전반에서 사용하는 커스텀 예외의 베이스 클래스
public class KHJException extends RuntimeException {

    private final ErrorCode errorCode;

    public KHJException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public KHJException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
