package site.wellmind.user.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.user.domain.vo.AddressVO;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "jsol_userinfo")
@Getter
@ToString(exclude = {"id"})
public class UserInfoModel extends BaseModel {

    @Id
    @Column(name = "USERINFO_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "address", column = @Column(name = "USER_ADDRESS")),
            @AttributeOverride(name = "address_detail", column = @Column(name = "USER_ADDRESS_DETAIL")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "USER_POSTAL_CODE"))
    })
    private AddressVO address;

    @Column(name = "USER_HOBBY")
    private String hobby;

    @Column(name = "USER_SIGNIFICANT")
    private String significant;

    @Column(name = "USER_PHOTO",nullable = true)
    private String photo;

    @Builder.Default
    @Column(name = "USER_IS_LONG")
    private boolean isLong=false;

    @Column(name = "HIRE_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime hireDate;

}
