package site.wellmind.user.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.security.util.EncryptionUtil;
import site.wellmind.transfer.domain.model.QDepartmentModel;
import site.wellmind.transfer.domain.model.QPositionModel;
import site.wellmind.transfer.domain.model.QTransferModel;
import site.wellmind.transfer.domain.model.TransferModel;
import site.wellmind.user.domain.dto.*;
import site.wellmind.user.domain.model.QUserTopModel;
import site.wellmind.user.domain.model.UserEducationModel;
import site.wellmind.user.domain.model.UserInfoModel;
import site.wellmind.user.domain.model.UserTopModel;
import site.wellmind.user.repository.UserEducationRepository;
import site.wellmind.user.repository.UserInfoRepository;
import site.wellmind.user.repository.UserTopRepository;
import site.wellmind.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User Service Implementation
 * <p>User Service Implementation</p>
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @see UserService
 * @see UserTopRepository
 * @since 2024-10-08
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserTopRepository userTopRepository;
    private final UserInfoRepository userInfoRepository;
    private final UserEducationRepository userEducationRepository;

    private final PasswordEncoder passwordEncoder;

    private final JPAQueryFactory queryFactory;
    private final QUserTopModel qUserTop=QUserTopModel.userTopModel;
    private final QTransferModel qTransfer=QTransferModel.transferModel;
    private final QPositionModel qPosition=QPositionModel.positionModel;
    private final QDepartmentModel qDepartment=QDepartmentModel.departmentModel;

    // //validate user request
    //        validateUserDto(dto);
    //
    //        //password encoding
    //        dto.setPassword(passwordEncoder.encode(dto.getPassword()));

    @Override
    @Transactional
    public UserDto save(UserDto dto) {

        UserTopModel savedUser = userTopRepository.save(UserTopModel.builder()
                .employeeId(dto.getEmployeeId())
                .email(dto.getEmail())
                .name(dto.getName())
                .phoneNum(dto.getPhoneNum())
                .authType("N")
                .regNumberFor(EncryptionUtil.encrypt(dto.getRegNumberFor()))
                .regNumberLat(EncryptionUtil.encrypt(dto.getRegNumberLat()))
                .deleteFlag(false)
                .build());

        userInfoRepository.save(UserInfoModel.builder()
                .address(dto.getUserInfo().getAddress())
                .photo(dto.getUserInfo().getPhoto())
                .hobby(dto.getUserInfo().getHobby())
                .isLong(dto.getUserInfo().isLong())
                .significant(dto.getUserInfo().getSignificant())
                .build());

        List<UserEducationModel> educationEntities = dto.getEducation().stream()
                .map(educationDto -> UserEducationModel.builder()
                        .degree(educationDto.getDegree())
                        .major(educationDto.getMajor())
                        .institutionName(educationDto.getInstitutionName())
                        .build())
                .collect(Collectors.toList());

        userEducationRepository.saveAll(educationEntities);
        //authType,role 은 관리자 체크 옵션을 통해 나중에 설정
        return UserDto.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .regDate(savedUser.getRegDate())
                .build();
    }

    @Override
    public List<UserDto> saveAll(List<UserDto> entities) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        if (existById(id)){
           userTopRepository.deleteById(id);
        }else{
            throw new GlobalException(ExceptionStatus.NOT_FOUND,"USERTOP_IDX not found");
        }

    }

    @Override
    public UserDto update(UserDto userDto) {
        return null;
    }

    @Override
    public UserDto findById(Long id) {
        UserTopModel userTopModel = userTopRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.NOT_FOUND, "UserTop not found"));
        UserInfoModel userInfoModel = userTopModel.getUserInfoModel();
        List<UserEducationModel> userEducations = userTopModel.getUserEduIds();

        return UserDto.builder()
                .id(userTopModel.getId())
                .email(userTopModel.getEmail())
                .phoneNum(userTopModel.getPhoneNum())
                .name(userTopModel.getName())
                .authType(userTopModel.getAuthType())
                .regNumberFor(userTopModel.getRegNumberFor())
                .regNumberLat(userTopModel.getRegNumberLat())
                .employeeId(userTopModel.getRegNumberLat())
                .userInfo(UserInfoDto.builder()
                        .photo(userInfoModel.getPhoto())
                        .address(userInfoModel.getAddress())
                        .significant(userInfoModel.getSignificant())
                        .isLong(userInfoModel.isLong())
                        .hobby(userInfoModel.getHobby())
                        .build())
                .education(userEducations.stream()
                        .map(edu -> EducationDto.builder()
                                .degree(edu.getDegree())
                                .institutionName(edu.getInstitutionName())
                                .major(edu.getMajor())
                                .build())
                        .collect(Collectors.toList()))
                .build();

    }

    @Override
    public List<UserDto> findAll() {
        List<UserTopModel> userTopModels = userTopRepository.findAll();

        return userTopModels.stream()
                .map(userTopModel -> {
                    UserInfoModel userInfoModel = userTopModel.getUserInfoModel(); // 이미 페치된 상태
                    List<UserEducationModel> userEducationModels = userTopModel.getUserEduIds();

                    return UserDto.builder()
                            .id(userTopModel.getId())
                            .email(userTopModel.getEmail())
                            .phoneNum(userTopModel.getPhoneNum())
                            .name(userTopModel.getName())
                            .authType(userTopModel.getAuthType())
                            .regNumberFor(userTopModel.getRegNumberFor())
                            .regNumberLat(userTopModel.getRegNumberLat())
                            .employeeId(userTopModel.getRegNumberLat())
                            .userInfo(UserInfoDto.builder()
                                    .photo(userInfoModel.getPhoto())
                                    .address(userInfoModel.getAddress())
                                    .significant(userInfoModel.getSignificant())
                                    .isLong(userInfoModel.isLong())
                                    .hobby(userInfoModel.getHobby())
                                    .build())
                            .education(userEducationModels.stream()
                                    .map(edu -> EducationDto.builder()
                                            .degree(edu.getDegree())
                                            .institutionName(edu.getInstitutionName())
                                            .major(edu.getMajor())
                                            .build())
                                    .collect(Collectors.toList()))
                            .build();

                }).collect(Collectors.toList());
    }

    @Override
    public boolean existById(Long id) {
        return userTopRepository.existsById(id);
    }

    @Override
    public Long count() {
        return null;
    }

    @Override
    public Boolean existByEmail(String email) {
        return userTopRepository.existsByEmail(email);
    }

    @Override
    public Boolean existByEmployeeId(String employeeId) {
        return userTopRepository.existsByEmployeeId(employeeId);
    }


    @Override
    public Page<UserDto> findBy(String departName, String positionName, String name, Pageable pageable) {
        BooleanBuilder whereClause = new BooleanBuilder();
        // 조건 추가 시 null 체크를 포함하여 안전하게 설정
        if (positionName != null) {
            whereClause.and(qPosition.name.eq(positionName));
        }

        if (departName != null) {
            whereClause.and(qDepartment.name.eq(departName));
        }

        if (name != null) {
            whereClause.and(qUserTop.name.containsIgnoreCase(name));
        }

        var user = queryFactory
                .selectFrom(qUserTop)
                .leftJoin(qUserTop.transferIds, qTransfer)
                .leftJoin(qTransfer.position, qPosition)
                .leftJoin(qTransfer.department, qDepartment)
                .where(whereClause)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qUserTop.id.desc())
                .fetch()
                .stream()
                .map(this::entityToDto)
                .toList();

        // Count query for pagination
        JPAQuery<Long> countQuery = queryFactory
                .select(qUserTop.count())
                .from(qUserTop)
                .leftJoin(qUserTop.transferIds, qTransfer)
                .leftJoin(qTransfer.position, qPosition)
                .leftJoin(qTransfer.department, qDepartment)
                .where(whereClause);

        return PageableExecutionUtils.getPage(user, pageable, countQuery::fetchOne);
    }

    @Override
    @Transactional
    public Boolean modifyByPassword(String oldPassword, String newPassword) {
        //if(passwordEncoder.matches(oldPassword,))
        return null;
    }

    private void validateUserDto(UserDto dto) {
        if (dto.getEmployeeId().isEmpty() || dto.getPassword().isEmpty()) {
            throw new GlobalException(ExceptionStatus.INVALID_INPUT, "employeeeId or password cannot be empty");
        }
    }


}
