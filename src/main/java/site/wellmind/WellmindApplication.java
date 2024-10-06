package site.wellmind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
/**
 * WellmindApplication
 * <p>QR Service Application</p>
 * @since 2024-10-05
 * @version 1.0
 * @author Seok Yuri(tjrdbfl)
 * @see SpringBootApplication
 */

@SpringBootApplication
@EnableJpaAuditing
public class WellmindApplication {

    public static void main(String[] args) {
        SpringApplication.run(WellmindApplication.class, args);
    }

}
