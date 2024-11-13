package site.wellmind.security.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.common.domain.vo.Role;
import site.wellmind.security.domain.vo.TokenStatus;

import java.time.LocalDateTime;

/**
 * PasswordSetupTokenModel
 * <p>비밀번호 생성 토큰 저장</p>
 * @since 2024-11-12
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "jsol_password_setup_token")
@ToString(exclude = {"id"})
public class PasswordSetupTokenModel extends BaseModel {
    @Id
    @Column(name = "PASSWORD_TOEKN_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PASSWORD_EMPLOYEE_ID",nullable = false)
    private String employeeId;

    @Column(name = "PASSWORD_TOKEN",nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "PASSWORD_TOKEN_STATUS")
    private TokenStatus tokenStatus;

    @Column(name = "TOKEN_EXPIRATION_TIME",nullable = false)
    private LocalDateTime expirationTime;

}
