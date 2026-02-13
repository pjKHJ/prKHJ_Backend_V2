package demago.khjv2.global.error.handler;

import demago.khjv2.global.error.ErrorResponse;
import demago.khjv2.global.error.GlobalErrorCode;
import demago.khjv2.global.error.exception.KHJException;
import demago.khjv2.global.feignclient.webhook.SendService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final SendService sendService;

    @ExceptionHandler(KHJException.class)
    public ResponseEntity<ErrorResponse> handleKHJException(KHJException e, HttpServletRequest request) {
        var ec = e.getErrorCode();
        String message = e.getMessage() != null ? e.getMessage() : ec.getMessage();

        try {
            sendService.sendDiscordAlert(e, request.getMethod(), request.getRequestURI());
        } catch (Exception ex) {
            log.warn("Discord 알림 전송 실패", ex);
        }

        return ResponseEntity
                .status(ec.getStatus())
                .body(ErrorResponse.of(ec.getStatus(), ec.getCode(), message, request.getRequestURI()));
    }

    // @Valid 바인딩 실패 (validation starter를 추가했을 때 동작)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + (fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(GlobalErrorCode.INVALID_REQUEST.getStatus())
                .body(ErrorResponse.of(GlobalErrorCode.INVALID_REQUEST.getStatus(), GlobalErrorCode.INVALID_REQUEST.getCode(), message, request.getRequestURI()));
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception e, HttpServletRequest request) {
        return ResponseEntity
                .status(GlobalErrorCode.INVALID_REQUEST.getStatus())
                .body(ErrorResponse.of(GlobalErrorCode.INVALID_REQUEST.getStatus(), GlobalErrorCode.INVALID_REQUEST.getCode(), GlobalErrorCode.INVALID_REQUEST.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException e, HttpServletRequest request) {
        return ResponseEntity
                .status(GlobalErrorCode.UNAUTHORIZED.getStatus())
                .body(ErrorResponse.of(GlobalErrorCode.UNAUTHORIZED, request.getRequestURI()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {
        return ResponseEntity
                .status(GlobalErrorCode.FORBIDDEN.getStatus())
                .body(ErrorResponse.of(GlobalErrorCode.FORBIDDEN, request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e, HttpServletRequest request) {
        // 서버 로그에만 스택 트레이스 남기고, 응답은 안전하게
        log.error("Unexpected exception: {} {}", request.getMethod(), request.getRequestURI(), e);
        sendService.sendDiscordAlert(e, request.getMethod(), request.getRequestURI());

        return ResponseEntity
                .status(GlobalErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponse.of(GlobalErrorCode.INTERNAL_SERVER_ERROR, request.getRequestURI()));
    }
}
