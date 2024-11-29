package site.wellmind.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import site.wellmind.common.domain.dto.Messenger;
import site.wellmind.common.domain.vo.ExceptionStatus;

/**
 * GlobalException
 * <p>ExceptionStatus를 활용해 전역적인 예외를 던지기 위한 래퍼 클래스</p>
 * <p>특정 도메인에 한정되지 않고, ExceptionStatus와 결합해 전역적으로 예외를 처리할 수 있는 구조적 틀을 제공한다.</p>
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-08
 */
@Getter
public class GlobalException extends RuntimeException{
    private final ExceptionStatus status;

    public GlobalException(ExceptionStatus status){
        super(status.getMessage());
        this.status=status;
    }

    public GlobalException(ExceptionStatus status,String message){
        super(message+ " : "+status.getMessage());
        this.status=status;
    }
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<Messenger> handleGlobalException(GlobalException ex) {

        return ResponseEntity
                .status(ex.getStatus().getHttpStatus())
                .body(Messenger.builder()
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Messenger> handleGeneralException(Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Messenger.builder()
                        .message("서버에서 처리 중 문제가 발생했습니다.")
                        .build());
    }
    public static GlobalException toGlobalException(Throwable e){
        return toGlobalException(e,ExceptionStatus.INTERNAL_SERVER_ERROR,"Global Handler Executed");
    }

    public static GlobalException toGlobalException(Throwable e,ExceptionStatus status,String message){
        return e.getClass().equals(GlobalException.class) ? (GlobalException) e:new GlobalException(status,message);
    }
}
