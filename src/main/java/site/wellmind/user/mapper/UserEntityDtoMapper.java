package site.wellmind.user.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import site.wellmind.security.util.EncryptionUtil;
import site.wellmind.transfer.domain.model.TransferModel;
import site.wellmind.user.domain.dto.*;
import site.wellmind.user.domain.model.*;
import site.wellmind.user.domain.vo.JobType;
import site.wellmind.user.domain.vo.MaritalType;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserEntityDtoMapper {
    private final EncryptionUtil encryptionUtil;

    public UserAllDto entityToDtoUserAll(UserTopModel model) {
        TransferModel transferModel = model.getTransferEmployeeIds().get(0);

        return UserAllDto.builder()
                .userTopDto(entityToDtoUserTop(model))
                .userInfo(entityToDtoUserInfo(model.getUserInfoModel()))
                .education(model.getUserEduIds().stream()
                        .map(this::entityToDtoUserEdu)
                        .collect(Collectors.toList()))
                .departName(transferModel.getDepartment().getName())
                .positionName(transferModel.getPosition().getName())
                .build();
    }

    public UserAllDto entityToDtoUserAll(AdminTopModel model) {
        TransferModel transferModel = model.getTransferIds().get(0);

        return UserAllDto.builder()
                .userTopDto(entityToDtoUserTop(model))
                .userInfo(entityToDtoUserInfo(model.getUserInfoModel()))
                .education(model.getUserEduIds().stream()
                        .map(this::entityToDtoUserEdu)
                        .collect(Collectors.toList()))
                .departName(transferModel.getDepartment().getName())
                .positionName(transferModel.getPosition().getName())
                .build();
    }

    public UserAllDto entityToDtoUserAllRest(UserTopModel model) {
        TransferModel transferModel = model.getTransferEmployeeIds().get(0);

        return UserAllDto.builder()
                .userTopDto(entityToDtoUserTopRest(model))
                .userInfo(entityToDtoUserInfoRest(model.getUserInfoModel()))
                .education(null)
                .departName(transferModel.getDepartment().getName())
                .positionName(transferModel.getPosition().getName())
                .build();
    }

    public UserAllDto entityToDtoUserAllRest(AdminTopModel model) {
        TransferModel transferModel = model.getTransferIds().get(0);

        return UserAllDto.builder()
                .userTopDto(entityToDtoUserTopRest(model))
                .userInfo(entityToDtoUserInfoRest(model.getUserInfoModel()))
                .education(null)
                .departName(transferModel.getDepartment().getName())
                .positionName(transferModel.getPosition().getName())
                .build();
    }


    public UserTopDto entityToDtoUserTop(AdminTopModel model) {
        return UserTopDto.builder()
                .id(model.getId())
                .employeeId(model.getEmployeeId())
                .email(model.getEmail())
                .name(model.getName())
                .phoneNum(model.getPhoneNum())
                .regNumberFor(encryptionUtil.decrypt(model.getRegNumberFor()))
                .regNumberLat(maskRegNumberLat(encryptionUtil.decrypt(model.getRegNumberLat())))
                .deleteFlag(model.getDeleteFlag())
                .authType(model.getAuthType())
                .regDate(model.getRegDate())
                .modDate(model.getModDate())
                .userInfoId(model.getUserInfoModel().getId())
                .roleId(model.getRole().getId())
                .build();
    }

    public UserTopDto entityToDtoUserTopRest(UserTopModel model) {
        return UserTopDto.builder()
                .id(model.getId())
                .employeeId(model.getEmployeeId())
                .email(model.getEmail())
                .name(model.getName())
                .phoneNum(model.getPhoneNum())
                .regNumberFor(null)
                .regNumberLat(null)
                .deleteFlag(model.getDeleteFlag())
                .authType(model.getAuthType())
                .regDate(model.getRegDate())
                .modDate(model.getModDate())
                .userInfoId(model.getUserInfoModel().getId())
                .roleId(model.getRole().getId())
                .build();
    }

    public UserTopDto entityToDtoUserTopRest(AdminTopModel model) {
        return UserTopDto.builder()
                .id(model.getId())
                .employeeId(model.getEmployeeId())
                .email(model.getEmail())
                .name(model.getName())
                .phoneNum(model.getPhoneNum())
                .regNumberFor(null)
                .regNumberLat(null)
                .deleteFlag(model.getDeleteFlag())
                .authType(model.getAuthType())
                .regDate(model.getRegDate())
                .modDate(model.getModDate())
                .userInfoId(model.getUserInfoModel().getId())
                .roleId(model.getRole().getId())
                .build();
    }


    public UserTopDto entityToDtoUserTop(UserTopModel model) {
        return UserTopDto.builder()
                .id(model.getId())
                .employeeId(model.getEmployeeId())
                .email(model.getEmail())
                .name(model.getName())
                .phoneNum(model.getPhoneNum())
                .regNumberFor(encryptionUtil.decrypt(model.getRegNumberFor()))
                .regNumberLat(maskRegNumberLat(encryptionUtil.decrypt(model.getRegNumberLat())))
                .deleteFlag(model.getDeleteFlag())
                .authType(model.getAuthType())
                .regDate(model.getRegDate())
                .modDate(model.getModDate())
                .userInfoId(model.getUserInfoModel().getId())
                .roleId(model.getRole().getId())
                .build();
    }


    public UserInfoDto entityToDtoUserInfo(UserInfoModel model) {
        return model==null? null:UserInfoDto.builder()
                .id(model.getId())
                .address(model.getAddress())
                .photo(model.getPhoto())
                .hobby(model.getHobby())
                .isLong(model.isLong())
                .significant(model.getSignificant())
                .hireDate(model.getHireDate())
                .build();
    }

    public UserInfoDto entityToDtoUserInfoRest(UserInfoModel model) {
        return model==null? null:UserInfoDto.builder()
                .id(model.getId())
                .address(model.getAddress())
                .photo(model.getPhoto())
                .hobby(null)
                .isLong(model.isLong())
                .significant(null)
                .hireDate(null)
                .build();
    }

    public UserSignificantDto entityToDtoUserSignificant(UserSignificantModel model) {
        return model==null? null:UserSignificantDto.builder()
                .id(model.getId())
                .maritalStatus(model.getMaritalStatus().getKorean())
                .smoker(model.getSmoker())
                .sleepHours(model.getSleepHours())
                .skipBreakfast(model.getSkipBreakfast())
                .chronicDiseases(model.getChronicDiseases())
                .jobCategory(model.getJobCategory().getKorean())
                .build();
    }


    public EducationDto entityToDtoUserEdu(UserEducationModel edu) {

        return edu==null? null:EducationDto.builder()
                .id(edu.getId())
                .degree(edu.getDegree())
                .institutionName(edu.getInstitutionName())
                .major(edu.getMajor())
                .regDate(edu.getRegDate())
                .modDate(edu.getModDate())
                .userId(edu.getUserTopModel() != null ? edu.getUserTopModel().getId() : null)
                .adminId(edu.getAdminTopModel() != null ? edu.getAdminTopModel().getId() : null)
                .build();
    }

    public ProfileDto entityToDtoUserProfile(UserTopModel user) {
        UserInfoModel userInfoModel = (user != null) ? user.getUserInfoModel() : null;
        TransferModel transferModel = (user != null && user.getTransferEmployeeIds() != null && !user.getTransferEmployeeIds().isEmpty())
                ? user.getTransferEmployeeIds().get(0)
                : null;

        return ProfileDto.builder()
                .employeeId(user != null ? user.getEmployeeId() : null)
                .email(user != null ? user.getEmail() : null)
                .name(user != null ? user.getName() : null)
                .phoneNum(user != null ? user.getPhoneNum() : null)
                .authType("N")
                .deleteFlag(user != null ? user.getDeleteFlag() : null)
                .photo(userInfoModel != null ? userInfoModel.getPhoto() : null)
                .address(userInfoModel != null ? userInfoModel.getAddress() : null)
                .departName(transferModel != null && transferModel.getDepartment() != null
                        ? transferModel.getDepartment().getName()
                        : null)
                .positionName(transferModel != null && transferModel.getPosition() != null
                        ? transferModel.getPosition().getName()
                        : null)
                .build();

    }

    public ProfileDto entityToDtoUserProfile(AdminTopModel admin) {
        UserInfoModel userInfoModel = admin != null ? admin.getUserInfoModel() : null;
        TransferModel transferModel = (admin != null && admin.getTransferIds() != null && !admin.getTransferIds().isEmpty())
                ? admin.getTransferIds().get(0)
                : null;

        return ProfileDto.builder()
                .employeeId(admin != null ? admin.getEmployeeId() : null)
                .email(admin != null ? admin.getEmail() : null)
                .name(admin != null ? admin.getName() : null)
                .phoneNum(admin != null ? admin.getPhoneNum() : null)
                .authType("M")
                .deleteFlag(admin != null ? admin.getDeleteFlag() : null)
                .photo(userInfoModel != null ? userInfoModel.getPhoto() : null)
                .address(userInfoModel != null ? userInfoModel.getAddress() : null)
                .departName(transferModel != null && transferModel.getDepartment() != null
                        ? transferModel.getDepartment().getName()
                        : null)
                .positionName(transferModel != null && transferModel.getPosition() != null
                        ? transferModel.getPosition().getName()
                        : null)
                .build();

    }

    public UserDetailDto entityToDtoUserDetail(UserTopModel user) {

        return UserDetailDto.builder()
                .employeeId(user.getEmployeeId())
                .regNumberFor(
                        (user.getRegNumberFor() != null && !user.getRegNumberFor().isEmpty())
                                ? encryptionUtil.decrypt(user.getRegNumberFor())
                                : null
                )
                .regNumberLat(
                        (user.getRegNumberLat() != null && !user.getRegNumberLat().isEmpty())
                                ? maskRegNumberLat(encryptionUtil.decrypt(user.getRegNumberLat()))
                                : null
                )
                .userInfo(entityToDtoUserInfo(user.getUserInfoModel()))
                .userSignificant(entityToDtoUserSignificant(user.getUserSignificantModel()))
                .education(user.getUserEduIds().stream()
                        .map(this::entityToDtoUserEdu)
                        .collect(Collectors.toList()))
                .build();
    }

    public UserDetailDto entityToDtoUserDetail(AdminTopModel admin) {
        UserInfoModel userInfoModel = admin.getUserInfoModel();
        List<UserEducationModel> userEducations = admin.getUserEduIds();

        return UserDetailDto.builder()
                .employeeId(admin.getEmployeeId())
                .regNumberFor(
                        (admin.getRegNumberFor() != null && !admin.getRegNumberFor().isEmpty())
                                ? encryptionUtil.decrypt(admin.getRegNumberFor())
                                : null
                )
                .regNumberLat(
                        (admin.getRegNumberLat() != null && !admin.getRegNumberLat().isEmpty())
                                ? maskRegNumberLat(encryptionUtil.decrypt(admin.getRegNumberLat()))
                                : null
                )
                .userInfo(entityToDtoUserInfo(userInfoModel))
                .userSignificant(entityToDtoUserSignificant(admin.getUserSignificantModel()))
                .education(userEducations.stream()
                        .map(this::entityToDtoUserEdu)
                        .collect(Collectors.toList()))
                .build();
    }

    public String maskRegNumberLat(String regNumberLat) {
        if (regNumberLat == null || regNumberLat.length() < 7) {
            throw new IllegalArgumentException("Invalid regNumberLat: must be a 7-character string");
        }
        // 앞 자리 하나와 나머지 자리수를 계산하여 *로 채움
        return regNumberLat.charAt(0) + "*".repeat(6);
    }
}