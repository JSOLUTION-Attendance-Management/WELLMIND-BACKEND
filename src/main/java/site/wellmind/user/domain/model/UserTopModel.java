package site.wellmind.user.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.common.domain.vo.Role;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString(exclude = {"id"})
public class UserTopModel extends BaseModel {

    @Id
    @Column(name = "USER_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_EMAIIL",unique = true,nullable = false)
    private String email;

    @Column(name = "USER_PASSWORD", unique = true)
    private String password;

    @Column(name = "USER_NAME")
    private String name;

    @Column(name="REG_NUMBER_FOR",nullable = false)
    private Integer regNumberFor;
    @Column(name = "REG_NUMBER_LAT",nullable = false)
    private Integer regNumberLat;

    @Column(name="USER_TEL")
    private String tel;

    @Column(name="AUTH_TYPE",length = 1)
    @Pattern(regexp = "[NM]",message = "authType must be 'N' or 'M'")
    private String authType="N";

    @Column(name = "AUTH_USER_LEVEL_CODE_ID")
    private Role authUserLevelCodeId;

    @Column(name = "DELETE_FLAG")
    private Boolean deleteFlag= false;  // Default to false


}
