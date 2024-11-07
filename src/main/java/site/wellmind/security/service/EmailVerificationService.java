package site.wellmind.security.service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import site.wellmind.common.service.MailService;
import site.wellmind.security.domain.model.EmailVerificationModel;
import site.wellmind.security.domain.vo.VerificationStatus;
import site.wellmind.security.repository.EmailVerificationRepository;
import site.wellmind.security.util.EncryptionUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * EmailVerificationService
 * <p>이메일 인증 관련 인증 코드 검증 로직</p>
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-06
 */
@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final MailService mailService;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordEncoder passwordEncoder;

    // 인증 코드 생성 및 이메일 발송
    @Transactional
    public String sendVerificationCode(String email) throws MessagingException{
        String code=mailService.createNumber(); //인증 코드 생성
        LocalDateTime expirationTime=LocalDateTime.now().plusMinutes(5);

        emailVerificationRepository.findByEmail(email).ifPresent(existingVerification -> {
            emailVerificationRepository.delete(existingVerification);
            emailVerificationRepository.flush();
        });

        emailVerificationRepository.save(EmailVerificationModel.builder()
                .email(email)
                .verificationCode(passwordEncoder.encode(code))
                .expirationTime(expirationTime)
                .build());

        mailService.sendEmailVerifyMessage(email,code); //메일 전송
        return code;
    }

    // 인증 코드 검증 로직
    @Transactional
    public VerificationStatus verifyCode(String email, String inputCode){
        EmailVerificationModel emailVerificationModel=emailVerificationRepository.findByEmail(email).orElse(null);

        if(emailVerificationModel!=null){
            if(emailVerificationModel.getExpirationTime().isBefore(LocalDateTime.now())){
                emailVerificationRepository.delete(emailVerificationModel);
                return VerificationStatus.EXPIRED;
            }
              if (passwordEncoder.matches(inputCode,emailVerificationModel.getVerificationCode())) {
            emailVerificationRepository.delete(emailVerificationModel);
            return VerificationStatus.SUCCESS;
            }
        }

        return VerificationStatus.INVALID_CODE;
    }

}

