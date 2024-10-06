package site.wellmind.common.domain.vo;

import lombok.AllArgsConstructor;

import java.util.stream.Stream;

@AllArgsConstructor
public enum Role {
    UML_77(0),UBL_66(1),UBL_55(2);

    private int roleCode;

    public static Role getRole(int roleCode){
        return Stream.of(values())
                .filter(i->i.roleCode==roleCode)
                .findFirst()
                .orElse(null);
    }

    public String getRoleName(){
        return switch (this){
            case UML_77 -> "Administrator Level 1";
            case UBL_66 -> "Administrator Level 2";
            case UBL_55 -> "Administrator Level 3";
            default -> "Unknown";
        };
    }
}
