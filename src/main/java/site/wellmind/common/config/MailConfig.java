package site.wellmind.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;
/**
 * MailConfig
 * <p>SMTP 를 위한 이메일 관련 설정 Config 클래스</p>
 * @since 2024-11-08
 * @version 1.0
 */

@Configuration
public class MailConfig {
    @Value("${mail.host}")
    private String mailHost;

    @Value("${mail.port}")
    private int mailPort;

    @Value("${mail.username}")
    private String mailUsername;

    @Value("${mail.password}")
    private String mailPassword;

    @Value("${mail.properties.mail.smtp.auth}")
    private boolean mailSmtpAuth;

    @Value("${mail.properties.mail.smtp.starttls.enable}")
    private boolean mailStartTls;

    @Value("${mail.properties.mail.smtp.connectiontimeout}")
    private int connectionTimeout;

    @Value("${mail.properties.mail.smtp.timeout}")
    private int timeout;

    @Value("${mail.properties.mail.smtp.writetimeout}")
    private int writeTimeout;

    @Bean
    public JavaMailSender javaMailSender(){
        JavaMailSenderImpl mailSender=new JavaMailSenderImpl();

        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);

        Properties props=mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", mailSmtpAuth);
        props.put("mail.smtp.starttls.enable", mailStartTls);
        props.put("mail.smtp.connectiontimeout", connectionTimeout);
        props.put("mail.smtp.timeout", timeout);
        props.put("mail.smtp.writetimeout", writeTimeout);

        return mailSender;
    }

}
