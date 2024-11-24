package site.wellmind.log.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import site.wellmind.log.domain.vo.DeleteStatus;
import site.wellmind.user.domain.model.AdminTopModel;

@Getter
public class UserDeletedEvent extends ApplicationEvent {
    private final Object userModel; // UserTopModel 또는 AdminTopModel
    private final String reason;
    private final DeleteStatus deleteType;
    private final AdminTopModel admin;

    public UserDeletedEvent(Object source, Object userModel, String reason, DeleteStatus deleteType, AdminTopModel admin) {
        super(source);
        this.userModel = userModel;
        this.reason = reason;
        this.deleteType = deleteType;
        this.admin = admin;
    }
}
