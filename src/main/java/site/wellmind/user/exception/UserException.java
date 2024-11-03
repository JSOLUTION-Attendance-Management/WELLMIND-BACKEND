package site.wellmind.user.exception;

import lombok.Getter;
import site.wellmind.common.domain.vo.ExceptionStatus;
/**
 * UserException
 * <p>ExceptionStatus를 활용해 사용자 정의 예외를 던지기 위한 래퍼 클래스</p>
 * <p>특정 도메인에 특화된 예외 처리를 가능하게 하고, ExceptionStatus와 결합해 전역적으로 예외를 처리할 수 있는 구조적 틀을 제공한다.</p>
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-02
 */
@Getter
public class UserException extends RuntimeException{

    private final ExceptionStatus status;
    public UserException(ExceptionStatus status){
        super(status.getMessage());  //status.getMessage()로부터 가져온 메시지를 RuntimeException에 전달하여 예외 메시지로 설정
        this.status=status;
    }

    public UserException(ExceptionStatus status, String message){
        super(message+" : "+status.getMessage());
        this.status=status;
    }

    // 주어진 예외를 UserException으로 변환하되, 기본 상태로 INTERNAL_SERVER_ERROR와 "Global Handler Executed" 메시지를 사용
    public static UserException toUserException(Throwable e){
        return toUserException(e,ExceptionStatus.INTERNAL_SERVER_ERROR,"Global Handler Executed");
    }

    //주어진 예외를 UserException으로 변환하되, 커스텀 상태와 메시지를 지정
    public static UserException toUserException(Throwable e,ExceptionStatus status,String message){
        return e.getClass().equals(UserException.class) ? (UserException) e:new UserException(status,message);
    }

}
