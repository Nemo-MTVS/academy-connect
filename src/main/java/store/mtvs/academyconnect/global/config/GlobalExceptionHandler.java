package store.mtvs.academyconnect.global.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외(Global Exception) 처리 클래스
 * - 컨트롤러 전역에서 발생하는 예외를 잡아서
 * - 에러 메시지를 사용자에게 반환해준다
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * IllegalArgumentException 예외 처리
     * - 예: 유효성 검증 실패, 잘못된 요청 등
     * - 400 Bad Request 상태 코드와 함께 에러 메시지 반환
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)  // HTTP 400 (Bad Request)
                .body(ex.getMessage());           // 에러 메시지를 응답 본문에 담아서 반환
    }

    /**
     * IllegalStateException 예외 처리
     * - 예: 잘못된 상태에서 메소드 호출 등
     * - 400 Bad Request 상태 코드와 함께 에러 메시지 반환
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)  // HTTP 400 (Bad Request)
                .body(ex.getMessage());
    }
}
