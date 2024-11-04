package site.wellmind.user.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AddressVO {
    private String address;  //대구시 수성구 효목로 19길
    private String address_detail;  //103동 1203호
    private String postalCode;

    @Override
    public String toString() {
        return address + " " + address_detail + " / "+postalCode;
    }
}
