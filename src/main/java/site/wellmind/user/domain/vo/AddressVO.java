package site.wellmind.user.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AddressVO {
    private String address;
    private String address_detail;
    private String postalCode;

    @Override
    public String toString() {
        return address + " " + address_detail + " / "+postalCode;
    }
}
