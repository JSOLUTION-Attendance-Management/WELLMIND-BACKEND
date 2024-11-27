package site.wellmind.transfer.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TransferType {
    PROMOTION("승진"),
    DEMOTION("강등"),
    TRANSFER("부서 이동"),
    SECONDMENT("파견"),
    TRANSFER_OUT("전출"),
    TRANSFER_IN("전입"),
    DISPATCH("외부 파견"),
    LEAVE("휴직"),
    RETURN("복직"),
    REHIRE("퇴직 후 재입사"),
    TERMINATION("퇴직"),
    LAYOFF("정리 해고"),
    INTERNSHIP("인턴 등록"),
    NEW_HIRE("신규 입사");

    private final String korean;

    public static TransferType fromKorean(String korean) {
        for (TransferType type : TransferType.values()) {
            if (type.getKorean().equals(korean)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown Korean value: " + korean);
    }
}
