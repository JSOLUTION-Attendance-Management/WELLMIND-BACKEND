package site.wellmind.common.domain.vo;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.wellmind.user.repository.UserTopRepository;
import site.wellmind.user.service.UserService;

/**
 * ExceptionStatus
 * <p>예외의 상태, 메시지, HTTP 코드 등을 정의하는 Enum 클래스</p>
 * <p>예외 상태와 메시지 정의를 일관되게 하고, 여러 도메인에서 재사용할 수 있는 상수 집합으로 사용한다.</p>
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-02
 */
@Getter
public enum ExceptionStatus {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request",4001),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "Invalid Input",4002),
    EXPIRED(HttpStatus.GONE,"Resource Expired",4101),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized",401),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED,"Expired Token",4102),
    INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED,"Invalid Token Signature",4103),
    MALFORMED_TOKEN(HttpStatus.BAD_REQUEST, "Malformed Token", 4104),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "No Permission",403),

    NOT_FOUND(HttpStatus.NOT_FOUND, "Not Found",404),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",500),
    ;

    private final HttpStatus httpStatus;
    private final String message;
    private final int errorCode;
    ExceptionStatus(HttpStatus httpStatus, String message, int errorCode) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.errorCode = errorCode;
    }
}
