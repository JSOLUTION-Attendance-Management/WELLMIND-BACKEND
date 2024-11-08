package site.wellmind.common.domain.vo;

import lombok.AllArgsConstructor;
import java.util.stream.Stream;

@AllArgsConstructor
public enum AdminRole {
    UML_77("UML_77"), // 최고 관리자
    UBL_66("UBL_66"), // 중간 관리자
    UBL_55("UBL_55"); // 하위 관리자

    private final String roleCode;

    public static AdminRole getAdminRole(String roleCode) {
        return Stream.of(values())
                .filter(i -> i.roleCode.equals(roleCode))
                .findFirst()
                .orElse(null);
    }

    public String getAdminRoleName() {
        return switch (this) {
            case UML_77 -> "Administrator Level 1";
            case UBL_66 -> "Administrator Level 2";
            case UBL_55 -> "Administrator Level 3";
            default -> "Unknown";
        };
    }

    public String getRoleCode() {
        return roleCode;
    }
}
