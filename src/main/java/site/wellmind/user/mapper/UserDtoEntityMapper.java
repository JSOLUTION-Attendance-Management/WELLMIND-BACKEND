package site.wellmind.user.mapper;

import org.springframework.stereotype.Component;
import site.wellmind.transfer.domain.model.TransferModel;
import site.wellmind.user.domain.dto.*;
import site.wellmind.user.domain.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDtoEntityMapper {
    public UserTopModel dtoToEntityUserAll(UserAllDto dto, UserInfoModel userInfoModel, AccountRoleModel accountRoleModel) {
        return UserTopModel.builder()
                .employeeId(dto.getUserTopDto().getEmployeeId())
                .email(dto.getUserTopDto().getEmail())
                .name(dto.getUserTopDto().getName())
                .phoneNum(dto.getUserTopDto().getPhoneNum())
                .authType("N")
                .regNumberFor(dto.getUserTopDto().getRegNumberFor())
                .regNumberLat(dto.getUserTopDto().getRegNumberLat())
                .deleteFlag(false)
                .userInfoModel(userInfoModel)
                .role(accountRoleModel)
                .build();
    }

    public UserTopModel dtoToEntityUserTop(UserTopDto dto) {
        return UserTopModel.builder()
                .id(dto.getId())
                .email(dto.getEmployeeId())
                .name(dto.getName())
                .phoneNum(dto.getPhoneNum())
                .regNumberFor(dto.getRegNumberFor())
                .regNumberLat(dto.getRegNumberLat())
                .deleteFlag(dto.isDeleteFlag())
                .authType(dto.getAuthType())
                .build();
    }

    public UserInfoModel dtoToEntityUserInfo(UserInfoDto dto) {
        return UserInfoModel.builder()
                .address(dto.getAddress())
                .photo(dto.getPhoto())
                .hobby(dto.getHobby())
                .isLong(dto.isLong())
                .significant(dto.getSignificant())
                .hireDate(dto.getHireDate())
                .build();

    }

    public UserEducationModel dtoToEntityUserEdu(EducationDto dto) {
        return UserEducationModel.builder()
                .degree(dto.getDegree())
                .major(dto.getMajor())
                .institutionName(dto.getInstitutionName())
                .userTopModel(dto.getUserTopModel())
                .adminTopModel(dto.getAdminTopModel())
                .build();
    }


}
