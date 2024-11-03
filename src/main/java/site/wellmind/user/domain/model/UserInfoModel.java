package site.wellmind.user.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.user.domain.vo.AddressVO;

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

    @Column(name = "USER_ADDRESS",unique = true)
    private AddressVO address;

    @Column(name = "USER_HOBBY")
    private String hobby;

    @Column(name = "USER_SIGNIFICANT")
    private String significant;

    @Column(name = "USER_PHOTO")
    private String photo;

    @Column(name = "USER_IS_LONG")
    private boolean isLong=false;
}
