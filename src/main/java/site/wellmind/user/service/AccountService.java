package site.wellmind.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.wellmind.common.service.CommandService;
import site.wellmind.common.service.QueryService;
import site.wellmind.transfer.domain.model.TransferModel;
import site.wellmind.user.domain.dto.*;
import site.wellmind.user.domain.model.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * AccountService
 * <p>User Service Interface</p>
 *
 * @version 1.0
 * @see QueryService
 * @see CommandService
 * @see UserAllDto
 * @since 2024-10-08
 */
public interface AccountService extends CommandService<UserAllDto>, QueryService<UserAllDto> {
    default UserTopModel dtoToEntityUserAll(UserAllDto dto, UserInfoModel userInfoModel, AccountRoleModel accountRoleModel) {
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

    default UserAllDto entityToDtoUserAll(UserTopModel model) {
        UserInfoModel userInfoModel = model.getUserInfoModel();
        List<UserEducationModel> userEducations = model.getUserEduIds();
        TransferModel transferModel = model.getTransferIds().get(0);
        String departName = transferModel.getDepartment().getName();
        String positionName = transferModel.getPosition().getName();

        return UserAllDto.builder()
                .userTopDto(entityToDtoUserTop(model))
                .userInfo(entityToDtoUserInfo(userInfoModel))
                .education(userEducations.stream()
                        .map(this::entityToDtoUserEdu)
                        .collect(Collectors.toList()))
                .departName(departName)
                .positionName(positionName)
                .build();
    }

    default UserAllDto entityToDtoUserAll(AdminTopModel model) {
        UserInfoModel userInfoModel = model.getUserInfoModel();
        List<UserEducationModel> userEducations = model.getUserEduIds();
        TransferModel transferModel = model.getTransferIds().get(0);
        String departName = transferModel.getDepartment().getName();
        String positionName = transferModel.getPosition().getName();

        return UserAllDto.builder()
                .userTopDto(entityToDtoUserTop(model))
                .userInfo(entityToDtoUserInfo(userInfoModel))
                .education(userEducations.stream()
                        .map(this::entityToDtoUserEdu)
                        .collect(Collectors.toList()))
                .departName(departName)
                .positionName(positionName)
                .build();
    }

    default UserTopModel dtoToEntityUserTop(UserTopDto dto) {
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

    default UserTopDto entityToDtoUserTop(AdminTopModel model) {
        return UserTopDto.builder()
                .id(model.getId())
                .email(model.getEmployeeId())
                .name(model.getName())
                .phoneNum(model.getPhoneNum())
                .regNumberFor(model.getRegNumberFor())
                .regNumberLat(model.getRegNumberLat())
                .deleteFlag(model.getDeleteFlag())
                .authType(model.getAuthType())
                .build();
    }

    default UserTopDto entityToDtoUserTop(UserTopModel model) {
        return UserTopDto.builder()
                .id(model.getId())
                .email(model.getEmployeeId())
                .name(model.getName())
                .phoneNum(model.getPhoneNum())
                .regNumberFor(model.getRegNumberFor())
                .regNumberLat(model.getRegNumberLat())
                .deleteFlag(model.getDeleteFlag())
                .authType(model.getAuthType())
                .regDate(model.getRegDate())
                .modDate(model.getModDate())
                .build();
    }

    default UserInfoModel dtoToEntityUserInfo(UserInfoDto dto) {
        return UserInfoModel.builder()
                .address(dto.getAddress())
                .photo(dto.getPhoto())
                .hobby(dto.getHobby())
                .isLong(dto.isLong())
                .significant(dto.getSignificant())
                .hireDate(dto.getHireDate())
                .build();

    }

    default UserInfoDto entityToDtoUserInfo(UserInfoModel model) {
        return UserInfoDto.builder()
                .address(model.getAddress())
                .photo(model.getPhoto())
                .hobby(model.getHobby())
                .isLong(model.isLong())
                .significant(model.getSignificant())
                .hireDate(model.getHireDate())
                .build();
    }

    default UserEducationModel dtoToEntityUserEdu(EducationDto dto, UserTopModel userTopModel) {
        return UserEducationModel.builder()
                .degree(dto.getDegree())
                .major(dto.getMajor())
                .institutionName(dto.getInstitutionName())
                .userTopModel(userTopModel)
                .build();
    }

    default EducationDto entityToDtoUserEdu(UserEducationModel edu) {
        return EducationDto.builder()
                .degree(edu.getDegree())
                .institutionName(edu.getInstitutionName())
                .major(edu.getMajor())
                .regDate(edu.getRegDate())
                .modDate(edu.getModDate())
                .build();
    }

    default ProfileDto entityToDtoUserProfile(UserTopModel user) {
        UserInfoModel userInfoModel = user.getUserInfoModel();
        TransferModel transferModel = user.getTransferIds().get(0);

        return ProfileDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .phoneNum(user.getPhoneNum())
                .authType("N")
                .photo(userInfoModel.getPhoto())
                .address(userInfoModel.getAddress())
                .departName(transferModel.getDepartment().getName())
                .positionName(transferModel.getPosition().getName())
                .build();

    }

    default ProfileDto entityToDtoUserProfile(AdminTopModel admin) {
        UserInfoModel userInfoModel = admin.getUserInfoModel();
        TransferModel transferModel = admin.getTransferIds().get(0);

        return ProfileDto.builder()
                .email(admin.getEmail())
                .name(admin.getName())
                .phoneNum(admin.getPhoneNum())
                .authType("M")
                .photo(userInfoModel.getPhoto())
                .address(userInfoModel.getAddress())
                .departName(transferModel.getDepartment().getName())
                .positionName(transferModel.getPosition().getName())
                .build();

    }

    default UserDetailDto entityToDtoUserDetail(UserTopModel user) {
        UserInfoModel userInfoModel = user.getUserInfoModel();
        List<UserEducationModel> userEducations = user.getUserEduIds();

        return UserDetailDto.builder()
                .employeeId(user.getEmployeeId())
                .regNumberFor(user.getRegNumberFor())
                .regNumberLat(user.getRegNumberLat())
                .deleteFlag(user.getDeleteFlag())
                .userInfo(entityToDtoUserInfo(userInfoModel))
                .education(userEducations.stream()
                        .map(this::entityToDtoUserEdu)
                        .collect(Collectors.toList()))
                .build();
    }

    default UserDetailDto entityToDtoUserDetail(AdminTopModel admin) {
        UserInfoModel userInfoModel = admin.getUserInfoModel();
        List<UserEducationModel> userEducations = admin.getUserEduIds();

        return UserDetailDto.builder()
                .employeeId(admin.getEmployeeId())
                .regNumberFor(admin.getRegNumberFor())
                .regNumberLat(admin.getRegNumberLat())
                .deleteFlag(admin.getDeleteFlag())
                .userInfo(entityToDtoUserInfo(userInfoModel))
                .education(userEducations.stream()
                        .map(this::entityToDtoUserEdu)
                        .collect(Collectors.toList()))
                .build();
    }

    Boolean existByEmail(String email);

    Boolean existByEmployeeId(AccountDto accountDto);

    Optional<UserTopModel> findUserByEmployeeId(String employeeId);

    Optional<AdminTopModel> findAdminByEmployeeId(String employeeId);

    Page<UserAllDto> findBy(String departName, String positionName, String name, Pageable pageable);

    Boolean modifyByPassword(String oldPassword, String newPassword);

    ProfileDto findProfileById(Long currentAccountId, boolean isAdmin);

    UserDetailDto findDetailById(Long currentAccountId, boolean isAdmin);
}

