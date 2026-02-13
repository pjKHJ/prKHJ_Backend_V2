package demago.khjv2.global.error;

import org.springframework.http.HttpStatus;

// 모든 커스텀 에러 코드는 이 인터페이스를 구현해서 사용합니다.
public interface ErrorCode {

    HttpStatus getStatus();

    String getCode();

    String getMessage();
}
