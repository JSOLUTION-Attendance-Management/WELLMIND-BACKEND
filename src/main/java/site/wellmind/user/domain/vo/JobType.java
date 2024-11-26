package site.wellmind.user.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum JobType {
    FIELD_WORKER("현장직"),
    OFFICE_WORKER("사무직");

    private final String korean;

    public static JobType fromKorean(String korean) {
        for (JobType type : JobType.values()) {
            if (type.getKorean().equals(korean)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown Korean value: " + korean);
    }
}
