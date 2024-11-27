package site.wellmind.user.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import site.wellmind.security.util.EncryptionUtil;
import site.wellmind.transfer.domain.model.TransferModel;
import site.wellmind.user.converter.MaritalTypeConverter;
import site.wellmind.user.domain.dto.*;
import site.wellmind.user.domain.model.*;
import site.wellmind.user.domain.vo.JobType;
import site.wellmind.user.domain.vo.MaritalType;
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
    private final EncryptionUtil encryptionUtil;

    public UserTopModel dtoToEntityUserAll(UserAllDto dto, UserInfoModel userInfoModel, AccountRoleModel accountRoleModel) {
        return UserTopModel.builder()
                .employeeId(dto.getUserTopDto().getEmployeeId())
                .email(dto.getUserTopDto().getEmail())
                .name(dto.getUserTopDto().getName())
                .phoneNum(dto.getUserTopDto().getPhoneNum())
                .authType("N")
                .regNumberFor(encryptionUtil.encrypt(dto.getUserTopDto().getRegNumberFor()))
                .regNumberLat(encryptionUtil.encrypt(dto.getUserTopDto().getRegNumberLat()))
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
                .regNumberFor(encryptionUtil.encrypt(dto.getRegNumberFor()))
                .regNumberLat(encryptionUtil.encrypt(dto.getRegNumberLat()))
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
                .regNumberFor(encryptionUtil.encrypt(dto.getRegNumberFor()))
                .regNumberLat(encryptionUtil.encrypt(dto.getRegNumberLat()))
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

    public UserSignificantModel dtoToEntitySignificant(UserSignificantDto dto){
        return UserSignificantModel.builder()
                .id(dto.getId())
                .maritalStatus(MaritalType.fromKorean(dto.getMaritalStatus()))
                .smoker(dto.getSmoker())
                .sleepHours(dto.getSleepHours())
                .skipBreakfast(dto.getSkipBreakfast())
                .chronicDiseases(dto.getChronicDiseases())
                .jobCategory(JobType.fromKorean(dto.getJobCategory()))
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
