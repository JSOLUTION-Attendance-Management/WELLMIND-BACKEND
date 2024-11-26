package site.wellmind.user.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MaritalType {
    SINGLE("미혼"),
    MARRIED("기혼");

    private final String korean;

    public static MaritalType fromKorean(String korean) {
        for (MaritalType type : MaritalType.values()) {
            if (type.getKorean().equals(korean)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown Korean value: " + korean);
    }
}
