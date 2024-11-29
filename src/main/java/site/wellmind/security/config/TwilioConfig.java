package site.wellmind.security.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {
    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Getter
    @Value("${twilio.service-sid}")
    private String serviceSid;
    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

}
