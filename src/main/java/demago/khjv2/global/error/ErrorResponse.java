package demago.khjv2.global.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String code,
        String message,
        String path
) {

    public static ErrorResponse of(HttpStatus status, String code, String message, String path) {
        return new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                code,
                message,
                path
        );
    }

    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return of(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage(), path);
    }
}
