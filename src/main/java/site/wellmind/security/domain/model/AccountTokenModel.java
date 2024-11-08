package site.wellmind.security.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.common.domain.vo.AdminRole;
import site.wellmind.common.domain.vo.Role;
import site.wellmind.security.domain.vo.TokenStatus;

import java.time.LocalDateTime;

/**
 * AccountTokenModel
 * <p>유저 토큰 정보 저장</p>
 * @since 2024-11-08
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "jsol_account_token")
@ToString(exclude = {"id"})
public class AccountTokenModel extends BaseModel {
    @Id
    @Column(name = "ACCOUNT_TOEKN_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ACCOUNT_EMPLOYEE_ID",unique = true,nullable = false)
    private String employeeId;

    @Column(name = "ACCOUNT_TOKEN",nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACCOUNT_TOKEN_ROLE")
    private Role roles;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACCOUNT_TOKEN_STATUS")
    private TokenStatus tokenStatus;

    @Column(name = "TOKEN_EXPIRATION_TIME",nullable = false)
    private LocalDateTime expirationTime;

}
