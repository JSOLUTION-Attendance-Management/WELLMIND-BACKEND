package site.wellmind.common.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import site.wellmind.common.domain.dto.MailDto;
import site.wellmind.security.provider.PasswordTokenProvider;
import site.wellmind.user.domain.dto.UserDto;

import java.util.Random;
/**
 * MailService
 * <p>이메일 인증, 비밀번호 설정, 재발급 관련 logic 을 위한 service layer</p>
 * @since 2024-11-06
 * @version 1.0
 * @see MailDto
 */
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final PasswordTokenProvider passwordTokenProvider;

    @Value("${mail.sender-email}")
    private String senderEmail;

    @Value("${app.domain}")
    private String appDomain;

    // 랜덤으로 숫자 생성
    public String createNumber(){
        Random random=new Random();
        StringBuilder key=new StringBuilder();

        for (int i=0;i<8;i++){
            int index= random.nextInt(3);
            switch (index){
                case 0->key.append((char) (random.nextInt(26)+97)); //소문자
                case 1->key.append((char) (random.nextInt(26)+65)); //대문자
                case 2->key.append(random.nextInt(10)); //숫자
            }
        }

        return key.toString();
    }

    public MimeMessage createEmailVerifyMail(String mail, String code) throws MessagingException{
        MimeMessage message=javaMailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO,mail);
        message.setSubject("이메일 인증");

        String body = "<div style='width: 100%; max-width: 600px; margin: auto; font-family: Arial, sans-serif; text-align: center;'>";
        body += "<img src='https://example.com/logo.png' alt='Company Logo' style='width: 100px; margin-bottom: 20px;'>"; // 로고 이미지 추가
        body += "<h2 style='color: #333;'>이메일 인증 요청</h2>";
        body += "<p style='font-size: 16px; color: #555;'>아래의 인증 번호를 사용하여 인증을 완료해 주세요.</p>";
        body += "<div style='padding: 20px; background-color: #f9f9f9; display: inline-block; border-radius: 5px; margin: 20px 0;'>";
        body += "<h1 style='margin: 0; color: #00AEEF;'>" + code + "</h1>"; // 하늘색 인증 코드 강조
        body += "</div>";
        body += "<p style='font-size: 14px; color: #777;'>이 인증 코드는 5분 동안만 유효합니다.</p>";
        body += "</div>";
        message.setText(body, "UTF-8", "html");

        return message;
    }

    // 메일 발송
    public String sendEmailVerifyMessage(String sendEmail,String code) throws MessagingException{

        MimeMessage message=createEmailVerifyMail(sendEmail,code); //메일 생성
        try{
            javaMailSender.send(message);
        }catch (MailException e){
            e.printStackTrace();
            throw new IllegalArgumentException("메일 발송 중 오류가 발생했습니다.");
        }

        return code; // 생성된 인증번호 반환
    }

    public MimeMessage createPasswordSetupMail(String email,String employeeId) throws MessagingException{
        MimeMessage message=javaMailSender.createMimeMessage();

        //비밀번호 설정 토큰 생성
        String token= passwordTokenProvider.generatePasswordSetupToken(employeeId);

        //비밀번호 설정 링크 생성
        String link=appDomain+"/password-setup?token="+token;

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO,email);
        message.setSubject("비밀번호 설정");

        String body = "<div style='width: 100%; max-width: 600px; margin: auto; font-family: Arial, sans-serif; text-align: center;'>";
        body += "<img src='https://example.com/logo.png' alt='Company Logo' style='width: 100px; margin-bottom: 20px;'>"; // 로고 이미지 추가
        body += "<h2 style='color: #333;'>비밀번호 설정</h2>";
        body += "<p style='font-size: 15px; color: #555;'>아래 링크를 클릭하여 비밀번호를 설정해 주세요.</p>";
        body += "<div style='padding: 20px; background-color: #f9f9f9; display: inline-block; border-radius: 5px; margin: 20px 0;'>";
        body += "<a href='" + link + "' style='color: #00AEEF; font-size: 16px;'>비밀번호 설정하기</a>"; // 링크
        body += "</div>";
        body += "<p style='font-size: 14px; color: #777;'>이 링크는 24시간 동안만 유효합니다.</p>";
        body += "</div>";
        message.setText(body, "UTF-8", "html");

        return message;
    }

    public void sendPasswordSetupEmail(String email,String employeeId) throws MessagingException{
        MimeMessage message = createPasswordSetupMail(email, employeeId);
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("비밀번호 설정 이메일 발송 중 오류가 발생했습니다.");
        }
    }
}

