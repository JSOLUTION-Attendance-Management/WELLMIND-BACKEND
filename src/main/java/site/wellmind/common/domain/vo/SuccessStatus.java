package site.wellmind.common.domain.vo;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessStatus {
    OK(HttpStatus.OK,"Ok",200),
    CREATED(HttpStatus.CREATED,"Created",201),
    ACCEPTED(HttpStatus.ACCEPTED,"Accepted",202),
    RESET_CONTENT(HttpStatus.RESET_CONTENT,"Reset Content",205);

    private final HttpStatus status;
    private final String message;
    private final int successCode;
    SuccessStatus(HttpStatus status, String message, int successCode) {
        this.status = status;
        this.message = message;
        this.successCode = successCode;
    }
}
