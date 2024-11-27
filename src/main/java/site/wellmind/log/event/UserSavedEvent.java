package site.wellmind.log.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import site.wellmind.user.domain.model.AdminTopModel;

import java.time.Clock;

@Getter
public class UserSavedEvent extends ApplicationEvent {
    private final String savedEmployeeId;
    private final AdminTopModel admin;

    public UserSavedEvent(Object source, String savedEmployeeId,AdminTopModel admin) {
        super(source);
        this.admin=admin;
        this.savedEmployeeId=savedEmployeeId;
    }
}
