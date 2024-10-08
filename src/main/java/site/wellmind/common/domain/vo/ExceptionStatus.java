package site.wellmind.common.domain.vo;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionStatus {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request",4001),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "Invalid Input",4002),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized",401),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "No Permission",403),

    NOT_FOUND(HttpStatus.NOT_FOUND, "Not Found",404),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",500),
    BAD_GATEWAY(HttpStatus.BAD_GATEWAY,"Bad Gateway",502)
    ;

    private final HttpStatus status;
    private final String message;
    private final int errorCode;
    ExceptionStatus(HttpStatus status, String message, int errorCode) {
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
    }
}
