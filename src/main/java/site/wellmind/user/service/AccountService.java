package site.wellmind.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.wellmind.common.service.CommandService;
import site.wellmind.common.service.QueryService;
import site.wellmind.security.util.EncryptionUtil;
import site.wellmind.transfer.domain.model.TransferModel;
import site.wellmind.user.domain.dto.EducationDto;
import site.wellmind.user.domain.dto.ProfileDto;
import site.wellmind.user.domain.dto.UserDto;
import site.wellmind.user.domain.dto.UserInfoDto;
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
 * @see UserDto
 * @since 2024-10-08
 */
public interface AccountService extends CommandService<UserDto>, QueryService<UserDto> {
    default UserTopModel dtoToEntityUserAll(UserDto dto, UserInfoModel userInfoModel, AccountRoleModel accountRoleModel) {
        return UserTopModel.builder()
                .employeeId(dto.getEmployeeId())
                .email(dto.getEmail())
                .name(dto.getName())
                .phoneNum(dto.getPhoneNum())
                .authType("N")
                .regNumberFor(EncryptionUtil.encrypt(dto.getRegNumberFor()))
                .regNumberLat(EncryptionUtil.encrypt(dto.getRegNumberLat()))
                .deleteFlag(false)
                .userInfoModel(userInfoModel)
                .role(accountRoleModel)
                .build();
    }

    default UserDto entityToDtoUserAll(UserTopModel model) {
        UserInfoModel userInfoModel = model.getUserInfoModel();
        List<UserEducationModel> userEducations = model.getUserEduIds();
        TransferModel transferModel=model.getTransferIds().get(0);
        String departName=transferModel.getDepartment().getName();
        String positionName=transferModel.getPosition().getName();

        return UserDto.builder()
                .id(model.getId())
                .employeeId(model.getEmployeeId())
                .email(model.getEmail())
                .name(model.getName())
                .phoneNum(model.getPhoneNum())
//                .regNumberFor(EncryptionUtil.decrypt(model.getRegNumberFor()))
//                .regNumberLat(EncryptionUtil.decrypt(model.getRegNumberLat()))
                .deleteFlag(model.getDeleteFlag())
                .authType(model.getAuthType())
                .regDate(model.getRegDate())
                .modDate(model.getModDate())
                .userInfo(entityToDtoUserInfo(userInfoModel))
                .education(userEducations.stream()
                        .map(this::entityToDtoUserEdu)
                        .collect(Collectors.toList()))
                .departName(departName)
                .positionName(positionName)
                .build();
    }
    default UserDto entityToDtoUserAll(AdminTopModel model) {
        UserInfoModel userInfoModel = model.getUserInfoModel();
        List<UserEducationModel> userEducations = model.getUserEduIds();
        TransferModel transferModel=model.getTransferIds().get(0);
        String departName=transferModel.getDepartment().getName();
        String positionName=transferModel.getPosition().getName();

        return UserDto.builder()
                .id(model.getId())
                .employeeId(model.getEmployeeId())
                .email(model.getEmail())
                .name(model.getName())
                .phoneNum(model.getPhoneNum())
//                .regNumberFor(EncryptionUtil.decrypt(model.getRegNumberFor()))
//                .regNumberLat(EncryptionUtil.decrypt(model.getRegNumberLat()))
                .deleteFlag(model.getDeleteFlag())
                .authType(model.getAuthType())
                .regDate(model.getRegDate())
                .modDate(model.getModDate())
                .userInfo(entityToDtoUserInfo(userInfoModel))
                .education(userEducations.stream()
                        .map(this::entityToDtoUserEdu)
                        .collect(Collectors.toList()))
                .departName(departName)
                .positionName(positionName)
                .build();
    }

    default UserInfoModel dtoToEntityUserInfo(UserInfoDto dto) {
        return UserInfoModel.builder()
                .address(dto.getAddress())
                .photo(dto.getPhoto())
                .hobby(dto.getHobby())
                .isLong(dto.isLong())
                .significant(dto.getSignificant())
                .build();

    }

    default UserInfoDto entityToDtoUserInfo(UserInfoModel model) {
        return UserInfoDto.builder()
                .address(model.getAddress())
                .photo(model.getPhoto())
                .hobby(model.getHobby())
                .isLong(model.isLong())
                .significant(model.getSignificant())
                .build();
    }

    default UserEducationModel dtoToEntityUserEdu(EducationDto dto,UserTopModel userTopModel){
        return UserEducationModel.builder()
                .degree(dto.getDegree())
                .major(dto.getMajor())
                .institutionName(dto.getInstitutionName())
                .userTopModel(userTopModel)
                .build();
    }
    default EducationDto entityToDtoUserEdu(UserEducationModel edu){
        return EducationDto.builder()
                .degree(edu.getDegree())
                .institutionName(edu.getInstitutionName())
                .major(edu.getMajor())
                .build();
    }
    
    default ProfileDto entityToDtoUserProfile(UserTopModel user){
        UserInfoModel userInfoModel = user.getUserInfoModel();
        TransferModel transferModel=user.getTransferIds().get(0);

        return ProfileDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .phoneNum(user.getPhoneNum())
                .authType("N")
                .phoneNum(user.getPhoneNum())
                .photo(userInfoModel.getPhoto())
                .address(userInfoModel.getAddress())
                .departName(transferModel.getDepartment().getName())
                .positionName(transferModel.getPosition().getName())
                .build();

    }
    default ProfileDto entityToDtoUserProfile(AdminTopModel admin){
        UserInfoModel userInfoModel = admin.getUserInfoModel();
        TransferModel transferModel=admin.getTransferIds().get(0);

        return ProfileDto.builder()
                .email(admin.getEmail())
                .name(admin.getName())
                .phoneNum(admin.getPhoneNum())
                .authType("M")
                .phoneNum(admin.getPhoneNum())
                .photo(userInfoModel.getPhoto())
                .address(userInfoModel.getAddress())
                .departName(transferModel.getDepartment().getName())
                .positionName(transferModel.getPosition().getName())
                .build();

    }
    Boolean existByEmail(String email);

    Boolean existByEmployeeId(String employeeId);

    Optional<UserTopModel> findUserByEmployeeId(String employeeId);

    Optional<AdminTopModel> findAdminByEmployeeId(String employeeId);

    Page<UserDto> findBy(String departName, String positionName, String name, Pageable pageable);

    Boolean modifyByPassword(String oldPassword, String newPassword);

    ProfileDto findProfileById(Long currentAccountId, boolean isAdmin);
}

