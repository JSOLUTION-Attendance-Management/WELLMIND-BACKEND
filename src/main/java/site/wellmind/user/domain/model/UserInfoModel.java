package site.wellmind.user.domain.model;

import jakarta.persistence.Entity;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@ToString(exclude = {"id"})
public class UserInfoModel extends BaseModel {

}
