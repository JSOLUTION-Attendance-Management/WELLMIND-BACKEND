package site.wellmind.security.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;

import java.time.LocalDateTime;

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
