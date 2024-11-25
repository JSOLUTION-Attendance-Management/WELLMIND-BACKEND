package site.wellmind.user.mapper;

import org.springframework.stereotype.Component;
import site.wellmind.transfer.domain.model.TransferModel;
import site.wellmind.user.domain.dto.*;
import site.wellmind.user.domain.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserEntityDtoMapper {

    public UserAllDto entityToDtoUserAll(UserTopModel model) {
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

    public UserAllDto entityToDtoUserAll(AdminTopModel model) {
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


    public UserTopDto entityToDtoUserTop(AdminTopModel model) {
        return UserTopDto.builder()
                .id(model.getId())
                .employeeId(model.getEmployeeId())
                .email(model.getEmail())
                .name(model.getName())
                .phoneNum(model.getPhoneNum())
                .regNumberFor(model.getRegNumberFor())
                .regNumberLat(model.getRegNumberLat())
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
                .regNumberFor(model.getRegNumberFor())
                .regNumberLat(model.getRegNumberLat())
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

    public ProfileDto entityToDtoUserProfile(AdminTopModel admin) {
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

    public UserDetailDto entityToDtoUserDetail(UserTopModel user) {
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

    public UserDetailDto entityToDtoUserDetail(AdminTopModel admin) {
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

}
