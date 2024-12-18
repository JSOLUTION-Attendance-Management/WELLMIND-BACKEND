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


    public UserTopDto entityToDtoUserTop(AdminTopModel model) {
        return UserTopDto.builder()
                .id(model.getId())
                .employeeId(model.getEmployeeId())
                .email(model.getEmail())
                .name(model.getName())
                .phoneNum(model.getPhoneNum())
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
        return UserInfoDto.builder()
                .id(model.getId())
                .address(model.getAddress())
                .photo(model.getPhoto())
                .hobby(model.getHobby())
                .isLong(model.isLong())
                .significant(model.getSignificant())
                .hireDate(model.getHireDate())
                .build();
    }

    public UserSignificantDto entityToDtoUserSignificant(UserSignificantModel model){
        return UserSignificantDto.builder()
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

        return EducationDto.builder()
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
        UserInfoModel userInfoModel = user.getUserInfoModel();
        TransferModel transferModel = user.getTransferEmployeeIds().get(0);

        return ProfileDto.builder()
                .employeeId(user.getEmployeeId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNum(user.getPhoneNum())
                .authType("N")
                .deleteFlag(user.getDeleteFlag())
                .photo(userInfoModel.getPhoto())
                .address(userInfoModel.getAddress())
                .departName(transferModel.getDepartment().getName())
                .positionName(transferModel.getPosition().getName())
                .build();

    }

    public ProfileDto entityToDtoUserProfile(AdminTopModel admin) {
        UserInfoModel userInfoModel = admin.getUserInfoModel();
        TransferModel transferModel = admin.getTransferIds().get(0);

        return ProfileDto.builder()
                .employeeId(admin.getEmployeeId())
                .email(admin.getEmail())
                .name(admin.getName())
                .phoneNum(admin.getPhoneNum())
                .authType("M")
                .deleteFlag(admin.getDeleteFlag())
                .photo(userInfoModel.getPhoto())
                .address(userInfoModel.getAddress())
                .departName(transferModel.getDepartment().getName())
                .positionName(transferModel.getPosition().getName())
                .build();

    }

    public UserDetailDto entityToDtoUserDetail(UserTopModel user) {

        return UserDetailDto.builder()
                .employeeId(user.getEmployeeId())
                .regNumberFor(encryptionUtil.decrypt(user.getRegNumberFor()))
                .regNumberLat(maskRegNumberLat(encryptionUtil.decrypt(user.getRegNumberLat())))
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
                .regNumberFor(encryptionUtil.decrypt(admin.getRegNumberFor()))
                .regNumberLat(maskRegNumberLat(encryptionUtil.decrypt(admin.getRegNumberLat())))
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
