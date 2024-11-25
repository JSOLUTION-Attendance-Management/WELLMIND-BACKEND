package site.wellmind.user.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import site.wellmind.transfer.domain.model.TransferModel;
import site.wellmind.user.domain.dto.*;
import site.wellmind.user.domain.model.*;
import site.wellmind.user.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserDtoEntityMapper {
    private final AccountRoleRepository accountRoleRepository;
    private final UserInfoRepository userInfoRepository;
    private final UserEducationRepository userEducationRepository;
    private final UserTopRepository userTopRepository;
    private final AdminTopRepository adminTopRepository;

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
        Optional<AccountRoleModel> accountRoleModel = accountRoleRepository.findById(dto.getRoleId());
        Optional<UserInfoModel> userInfoModel = userInfoRepository.findById(dto.getUserInfoId());

        return UserTopModel.builder()
                .id(dto.getId())
                .employeeId(dto.getEmployeeId())
                .email(dto.getEmail())
                .name(dto.getName())
                .phoneNum(dto.getPhoneNum())
                .regNumberFor(dto.getRegNumberFor())
                .regNumberLat(dto.getRegNumberLat())
                .deleteFlag(dto.isDeleteFlag())
                .authType(dto.getAuthType())
                .role(accountRoleModel.orElse(null))
                .userInfoModel(userInfoModel.orElse(null))
                .build();
    }

    public AdminTopModel dtoToEntityAdminTop(UserTopDto dto) {
        Optional<AccountRoleModel> accountRoleModel = accountRoleRepository.findById(dto.getRoleId());
        Optional<UserInfoModel> userInfoModel = userInfoRepository.findById(dto.getUserInfoId());

        return AdminTopModel.builder()
                .id(dto.getId())
                .employeeId(dto.getEmployeeId())
                .email(dto.getEmail())
                .name(dto.getName())
                .phoneNum(dto.getPhoneNum())
                .regNumberFor(dto.getRegNumberFor())
                .regNumberLat(dto.getRegNumberLat())
                .deleteFlag(dto.isDeleteFlag())
                .authType(dto.getAuthType())
                .role(accountRoleModel.orElse(null))
                .authAdminLevelCodeId(dto.getAuthAdminLevelCodeId())
                .userInfoModel(userInfoModel.orElse(null))
                .build();
    }

    public UserInfoModel dtoToEntityUserInfo(UserInfoDto dto) {
        return UserInfoModel.builder()
                .id(dto.getId())
                .address(dto.getAddress())
                .photo(dto.getPhoto())
                .hobby(dto.getHobby())
                .isLong(dto.isLong())
                .significant(dto.getSignificant())
                .hireDate(dto.getHireDate())
                .build();

    }

    public UserEducationModel dtoToEntityUserEdu(EducationDto dto) {
        Optional<UserTopModel> user = userTopRepository.findById(dto.getId());
        Optional<AdminTopModel> admin = adminTopRepository.findById(dto.getId());

        return UserEducationModel.builder()
                .id(dto.getId())
                .degree(dto.getDegree())
                .major(dto.getMajor())
                .institutionName(dto.getInstitutionName())
                .userTopModel(user.orElse(null))
                .adminTopModel(admin.orElse(null))
                .build();
    }


}
