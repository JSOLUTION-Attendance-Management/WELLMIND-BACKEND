package site.wellmind.common.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.wellmind.common.domain.dto.Messenger;
import site.wellmind.common.domain.vo.ExceptionStatus;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Messenger> handleAccessDeniedException(AccessDeniedException ex){
        return ResponseEntity.status(ExceptionStatus.NO_PERMISSION.getHttpStatus())
                .body(Messenger.builder()
                        .message(ExceptionStatus.NO_PERMISSION.getMessage()).build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Messenger> handleGeneralException(Exception ex){
        return ResponseEntity.status(ExceptionStatus.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(Messenger.builder()
                        .message(ExceptionStatus.INTERNAL_SERVER_ERROR.getMessage()).build());
    }
}