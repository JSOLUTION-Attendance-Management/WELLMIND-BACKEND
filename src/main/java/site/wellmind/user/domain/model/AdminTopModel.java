package site.wellmind.user.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.common.domain.vo.AdminRole;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "jsol_admintop")
@ToString(exclude = {"id"})
public class AdminTopModel extends BaseModel {
    @Id
    @Column(name = "ADMIN_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ADMIN_EMPLOYEE_ID",unique = true,nullable = false)
    private String employeeId;

    @Column(name = "ADMIN_EMAIL",unique = true,nullable = false)
    private String email;

    @Column(name = "ADMIN_PASSWORD", unique = true)
    private String password;

    @Column(name = "ADMIN_NAME")
    private String name;

    @Column(name="REG_NUMBER_FOR",nullable = false)
    private String regNumberFor;
    @Column(name = "REG_NUMBER_LAT",nullable = false)
    private String regNumberLat;

    @Column(name="ADMIN_PHONE_NUM",length = 50)
    private String phoneNum;

    @Column(name="AUTH_TYPE",length = 1)
    @Pattern(regexp = "[NM]",message = "authType must be 'N' or 'M'")
    private String authType="M";

    @Enumerated(EnumType.STRING)
    @Column(name = "AUTH_ADMIN_LEVEL_CODE_ID")
    private AdminRole authAdminLevelCodeId;

    @Column(name = "DELETE_FLAG")
    private Boolean deleteFlag= false;

}
