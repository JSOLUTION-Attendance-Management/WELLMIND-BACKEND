package site.wellmind.security.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;

import java.time.LocalDateTime;

/**
 * EmailVerificationModel
 * <p>이메일 인증 처리를 위한 모델</p>
 * @since 2024-11-07
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "jsol_email_verification")
@ToString(exclude = {"id"})
public class EmailVerificationModel extends BaseModel {

    @Id
    @Column(name = "EMAIL_VERIFY_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "VERIFY_EMAIL",unique = true,nullable = false)
    private String email;

    @Column(name = "VERIFY_CODE",nullable = false)
    private String verificationCode;

    @Column(name = "VERIFY_EXPIRATION_TIME",nullable = false)
    private LocalDateTime expirationTime;
}
