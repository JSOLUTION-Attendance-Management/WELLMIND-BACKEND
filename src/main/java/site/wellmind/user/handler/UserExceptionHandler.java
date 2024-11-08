package site.wellmind.user.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import site.wellmind.common.exception.GlobalException;
/**
 * UserExceptionHandler
 * <p> Spring의 전역 예외 처리기/p>
 * <p>특정 도메인에 특화된 예외 처리를 가능하게 하고, ExceptionStatus와 결합해 전역적으로 예외를 처리할 수 있는 구조적 틀을 제공한다.</p>
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-02
 */
@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice(basePackages = "site.wellmind.user")
public class UserExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request){
        log.error("@UserExceptionHandler(Exception.class) 에러 내용 : {} {}",ex,request);
        GlobalException result= GlobalException.toGlobalException(ex);
        return ResponseEntity.status(result.getStatus().getHttpStatus()).body(result.getMessage());
    }
}
