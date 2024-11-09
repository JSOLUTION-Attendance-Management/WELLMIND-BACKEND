package site.wellmind.common.domain.vo;

import lombok.AllArgsConstructor;
import site.wellmind.security.provider.JwtTokenProvider;

import java.util.stream.Stream;
/**
 * Role
 * <p>Integrated roles : user role + admin roles</p>
 * @see AdminRole
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-09
 */
@AllArgsConstructor
public enum Role {
    ROLE_USER("USER"),              // 일반 사용자
    ROLE_ADMIN_UML_77("ADMIN_UML_77"), // 최고 관리자
    ROLE_ADMIN_UBL_66("ADMIN_UBL_66"), // 중간 관리자
    ROLE_ADMIN_UBL_55("ADMIN_UBL_55"); // 하위 관리자

    private final String roleCode;

    public static Role getRole(String roleCode){
        return Stream.of(values())
                .filter(i -> i.roleCode.equals(roleCode))
                .findFirst()
                .orElse(null);
    }

    public String getRoleName(){
        return switch (this){
            case ROLE_USER -> "User Level";
            case ROLE_ADMIN_UML_77 -> "Administrator Level 1";
            case ROLE_ADMIN_UBL_66 -> "Administrator Level 2";
            case ROLE_ADMIN_UBL_55 -> "Administrator Level 3";
            default -> "Unknown";
        };
    }

    public String getRoleCode() {
        return roleCode;
    }
}
