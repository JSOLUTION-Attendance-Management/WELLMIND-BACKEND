package site.wellmind.user.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;

/**
 * AccountRoleModel
 * <p>account role 테이블</p>
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-16
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "jsol_account_role")
@ToString(exclude = {"id"})
public class AccountRoleModel extends BaseModel {
    @Id
    @Column(name = "ROLE_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ROLE_ID",nullable = false,length = 50)
    private String roleId;

    @Column(name = "ROLE_KNM",nullable = false,length = 200)
    private String roleKoreanNamDe;

    @Column(name = "ROLE_GROUP_LEVEL",nullable = false,length = 50)
    private String roleGroupLevel;

    @Column(name = "ROLE_AUTH_TYPE",nullable = false,length = 50)
    private String roleAuthType;

    @Column(name = "ROLE_LEVEL",unique = true,nullable = false)
    private Integer roleLevel;

    @Builder.Default
    @Column(name = "DELETE_FLAG")
    private Boolean deleteFlag= false;

}
