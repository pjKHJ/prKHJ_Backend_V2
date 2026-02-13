package demago.khjv2.global.error;

import org.springframework.http.HttpStatus;

public enum GlobalErrorCode implements ErrorCode {

    // 400
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "GLB_400", "잘못된 요청입니다."),
    INVALID_PART_DATA(HttpStatus.BAD_REQUEST, "GLB_400", "유효하지 않는 부품 데이터 입니다."),

    // 401/403
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "GLB_401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "GLB_403", "권한이 없습니다."),
    PROJECT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "GLB_403", "프로젝트 접근 권한이 없습니다."),

    // 404
    NOT_FOUND(HttpStatus.NOT_FOUND, "GLB_404", "리소스를 찾을 수 없습니다."),
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "GLB_404", "프로젝트를 찾을 수 없습니다."),
    PROJECT_DELETE_FAILED(HttpStatus.NOT_FOUND, "GLB_404", "프로젝트 삭제에 실패했습니다."),
    PART_NOT_FOUND(HttpStatus.NOT_FOUND, "GLB_404", "부품을 찾을 수 없습니다."),

    // 409
    CONFLICT(HttpStatus.CONFLICT, "GLB_409", "요청이 충돌했습니다."),
    PROJECT_NAME_DUPLICATE(HttpStatus.CONFLICT, "GLB_409", "이미 존재하는 프로젝트 이름입니다."),

    // 500
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "GLB_500", "이미지 업로드에 실패했습니다."),
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
