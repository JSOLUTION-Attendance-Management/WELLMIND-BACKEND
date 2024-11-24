package site.wellmind.user.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.common.service.UtilService;
import site.wellmind.log.domain.model.LogArchiveDeleteDetailModel;
import site.wellmind.log.domain.model.LogArchiveDeleteModel;
import site.wellmind.log.domain.model.LogArchiveUpdateModel;
import site.wellmind.log.domain.vo.DeleteStatus;
import site.wellmind.log.event.UserDeletedEvent;
import site.wellmind.log.repository.LogArchiveDeleteDetailRepository;
import site.wellmind.log.repository.LogArchiveDeleteRepository;
import site.wellmind.log.repository.LogArchiveUpdateRepository;
import site.wellmind.security.util.EncryptionUtil;
import site.wellmind.transfer.domain.model.QDepartmentModel;
import site.wellmind.transfer.domain.model.QPositionModel;
import site.wellmind.transfer.domain.model.QTransferModel;
import site.wellmind.user.domain.dto.*;
import site.wellmind.user.domain.model.*;
import site.wellmind.user.mapper.UserDtoEntityMapper;
import site.wellmind.user.mapper.UserEntityDtoMapper;
import site.wellmind.user.repository.*;
import site.wellmind.user.service.AccountService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User Service Implementation
 * <p>User Service Implementation</p>
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @see AccountService
 * @see UserTopRepository
 * @see UserInfoRepository
 * @see UserEducationRepository
 * @since 2024-10-08
 */
@Service
@RequiredArgsConstructor
@Slf4j(topic = "AccountServiceImpl")
public class AccountServiceImpl implements AccountService {

    private final UserTopRepository userTopRepository;
    private final UserInfoRepository userInfoRepository;
    private final UserEducationRepository userEducationRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final AdminTopRepository adminTopRepository;
    private final LogArchiveUpdateRepository logArchiveUpdateRepository;
    private final LogArchiveDeleteRepository logArchiveDeleteRepository;
    private final LogArchiveDeleteDetailRepository logArchiveDeleteDetailRepository;

    private final UtilService utilService;
    private final EncryptionUtil encryptionUtil;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    private final UserDtoEntityMapper userDtoEntityMapper;
    private final UserEntityDtoMapper userEntityDtoMapper;

    private final JPAQueryFactory queryFactory;
    private final QUserTopModel qUserTop = QUserTopModel.userTopModel;
    private final QAdminTopModel qAdminTop = QAdminTopModel.adminTopModel;
    private final QTransferModel qTransfer = QTransferModel.transferModel;
    private final QPositionModel qPosition = QPositionModel.positionModel;
    private final QDepartmentModel qDepartment = QDepartmentModel.departmentModel;

    @Override
    @Transactional
    public Object save(UserAllDto dto) {

        try {

            UserInfoModel userInfoModel = userInfoRepository.save(userDtoEntityMapper.dtoToEntityUserInfo(dto.getUserInfo()));

            AccountRoleModel accountRoleModel = accountRoleRepository.findByRoleId("UGL_11");
            //UserTopModel savedUser = userTopRepository.save(dtoToEntity(dto));
            dto.getUserTopDto().setRegNumberFor(encryptionUtil.encrypt(dto.getUserTopDto().getRegNumberFor()));
            dto.getUserTopDto().setRegNumberLat(encryptionUtil.encrypt(dto.getUserTopDto().getRegNumberLat()));
            UserTopModel savedUser = userTopRepository.save(userDtoEntityMapper.dtoToEntityUserAll(dto, userInfoModel, accountRoleModel));

            List<UserEducationModel> educationEntities = dto.getEducation().stream()
                    .map(educationDto -> userDtoEntityMapper.dtoToEntityUserEdu(educationDto, savedUser))
                    .collect(Collectors.toList());

            userEducationRepository.saveAll(educationEntities);


            return UserTopDto.builder()
                    .id(savedUser.getId())
                    .email(savedUser.getEmail())
                    .employeeId(savedUser.getEmployeeId())
                    .name(savedUser.getName())
                    .authType("N")
                    .regDate(savedUser.getRegDate())
                    .build();
        } catch (Exception e) {
            throw new GlobalException(ExceptionStatus.INTERNAL_SERVER_ERROR, "Failed to save user data for employee ID : " + dto.getUserTopDto().getEmployeeId());
        }

    }

    @Override
    public List<UserAllDto> saveAll(List<UserAllDto> entities) {
        return null;
    }

    @Async
    @Override
    public void deleteById(Object ob, AccountDto accountDto) {
        UserDeleteDto userDeleteDto = (UserDeleteDto) ob;
        Optional<AdminTopModel> admin = findAdminByEmployeeId(accountDto.getEmployeeId());
        Optional<UserTopModel> userTopModel = findUserByEmployeeId(userDeleteDto.getEmployeeId());
        if (userTopModel.isPresent()) {  //삭제하려는 대상이 사용자일 경우
            UserTopModel user = userTopModel.get();
            if (!user.getDeleteFlag()) { //논리 삭제의 경우
                user.setDeleteFlag(true);
                userTopRepository.save(user);
                //saveDeleteLog(userTopModel.get(), userDeleteDto.getDeletedReason(), DeleteStatus.CACHE, admin.get());
                eventPublisher.publishEvent(new UserDeletedEvent(this, user, userDeleteDto.getDeletedReason(), DeleteStatus.CACHE, admin.get()));

            } else {  //완전 삭제의 경우
                //saveDeleteLog(userTopModel.get(), userDeleteDto.getDeletedReason(), DeleteStatus.CLEAR, admin.get());
                eventPublisher.publishEvent(new UserDeletedEvent(this, user, userDeleteDto.getDeletedReason(), DeleteStatus.CLEAR, admin.get()));
                userTopRepository.delete(user);
                userInfoRepository.delete(user.getUserInfoModel());
                List<UserEducationModel> userEducationModels=user.getUserEduIds();
                userEducationRepository.deleteAll(userEducationModels);
            }
        } else {  //삭제하려는 대상이 관리자일 경우
            Optional<AdminTopModel> adminTopModel = findAdminByEmployeeId(userDeleteDto.getEmployeeId());
            if (!adminTopModel.isPresent()) {
                AdminTopModel adminUser = adminTopModel.get();
                if (!adminUser.getDeleteFlag()) { //논리 삭제의 경우
                    adminUser.setDeleteFlag(true);
                    adminTopRepository.save(adminUser);
                   // saveDeleteLog(adminTopModel.get(), userDeleteDto.getDeletedReason(), DeleteStatus.CACHE, admin.get());
                    eventPublisher.publishEvent(new UserDeletedEvent(this, adminUser, userDeleteDto.getDeletedReason(), DeleteStatus.CACHE, admin.get()));

                } else {  //완전 삭제인 경우
                    //saveDeleteLog(adminTopModel.get(), userDeleteDto.getDeletedReason(), DeleteStatus.CLEAR, admin.get());
                    eventPublisher.publishEvent(new UserDeletedEvent(this, adminUser, userDeleteDto.getDeletedReason(), DeleteStatus.CLEAR, admin.get()));

                    adminTopRepository.delete(adminUser);
                    userInfoRepository.delete(adminUser.getUserInfoModel());
                    List<UserEducationModel> userEducationModels=adminUser.getUserEduIds();
                    userEducationRepository.deleteAll(userEducationModels);
                }
            } else {
                throw new GlobalException(ExceptionStatus.ACCOUNT_NOT_FOUND);
            }
        }
    }

//    private void saveDeleteLog(UserTopModel user, String reason, DeleteStatus deleteType, AdminTopModel admin) {
//        try {
//            LogArchiveDeleteModel masterLog = saveMasterDeleteLog(reason, deleteType, admin);
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.registerModule(new JavaTimeModule());
//
//            saveDeleteDetailLog(masterLog, "jsol_usertop", user.getEmployeeId(), objectMapper.writeValueAsString(entityToDtoUserTop(user)));
//
//            if (user.getUserInfoModel() != null) {
//                saveDeleteDetailLog(masterLog, "jsol_userinfo", user.getEmployeeId(), objectMapper.writeValueAsString(entityToDtoUserInfo(user.getUserInfoModel())));
//            }
//
//            if (user.getUserEduIds() != null) {
//                String educationDetails = objectMapper.writeValueAsString(user.getUserEduIds().stream()
//                        .map(this::entityToDtoUserEdu)
//                        .collect(Collectors.toList()));
//                saveDeleteDetailLog(masterLog, "jsol_user_education", user.getEmployeeId(), educationDetails);
//            }
//
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Error saving delete log", e);
//        }
//    }

//    private void saveDeleteLog(AdminTopModel user, String reason, DeleteStatus deleteType, AdminTopModel admin) {
//        try {
//            LogArchiveDeleteModel masterLog = saveMasterDeleteLog(reason, deleteType, admin);
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.registerModule(new JavaTimeModule());
//
//            saveDeleteDetailLog(masterLog, "jsol_usertop", user.getEmployeeId(), objectMapper.writeValueAsString(entityToDtoUserTop(user)));
//
//            if (user.getUserInfoModel() != null) {
//                saveDeleteDetailLog(masterLog, "jsol_userinfo", user.getEmployeeId(), objectMapper.writeValueAsString(entityToDtoUserInfo(user.getUserInfoModel())));
//            }
//
//            if (user.getUserEduIds() != null) {
//                String educationDetails = objectMapper.writeValueAsString(user.getUserEduIds().stream()
//                        .map(this::entityToDtoUserEdu)
//                        .collect(Collectors.toList()));
//                saveDeleteDetailLog(masterLog, "jsol_user_education", user.getEmployeeId(), educationDetails);
//            }
//
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Error saving delete log", e);
//        }
//    }
//    private LogArchiveDeleteModel saveMasterDeleteLog(String reason, DeleteStatus deleteType, AdminTopModel admin) {
//        LogArchiveDeleteModel masterLog = LogArchiveDeleteModel.builder()
//                .deleteReason(reason)
//                .deleteType(deleteType)
//                .deleterId(admin)
//                .build();
//        return logArchiveDeleteRepository.save(masterLog);
//    }
//
//    private void saveDeleteDetailLog(LogArchiveDeleteModel masterLog, String tableName, String employeeId, String deletedValue) {
//        LogArchiveDeleteDetailModel detailLog = LogArchiveDeleteDetailModel.builder()
//                .masterDeleteLogId(masterLog)
//                .tableName(tableName)
//                .deletedEmployeeId(employeeId)
//                .deletedValue(deletedValue)
//                .build();
//        logArchiveDeleteDetailRepository.save(detailLog);
//    }

    @Override
    public Object findById(String employeeId, AccountDto accountDto) {
        Long currentAccountId = accountDto.getAccountId();

        if (!accountDto.isAdmin()) {  //사용자
            UserTopModel userTopModel = userTopRepository.findById(currentAccountId)
                    .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND, ExceptionStatus.USER_NOT_FOUND.getMessage()));
            return userEntityDtoMapper.entityToDtoUserAll(userTopModel);
        } else { //관리자

            if (employeeId != null) {
                Optional<UserTopModel> user = findUserByEmployeeId(employeeId);
                log.info("user : {}", user);

                if (!user.isEmpty()) {
                    if (accountDto.getRole().equals("ROLE_ADMIN_UBL_55")) {
                        return userEntityDtoMapper.entityToDtoUserProfile(user.get());
                    } else if (accountDto.getRole().equals("ROLE_ADMIN_UBL_66")) {
                        return userEntityDtoMapper.entityToDtoUserAll(user.get());
                    } else {
                        throw new GlobalException(ExceptionStatus.UNAUTHORIZED, ExceptionStatus.UNAUTHORIZED.getMessage());
                    }
                }
                Optional<AdminTopModel> admin = findAdminByEmployeeId(employeeId);
                log.info("admin : {}", admin);

                if (!admin.isEmpty()) {
                    if (accountDto.getRole().equals("ROLE_ADMIN_UBL_55")) {
                        return userEntityDtoMapper.entityToDtoUserProfile(admin.get());
                    } else if (accountDto.getRole().equals("ROLE_ADMIN_UBL_66")) {
                        return userEntityDtoMapper.entityToDtoUserAll(admin.get());
                    } else {
                        throw new GlobalException(ExceptionStatus.ADMIN_NOT_FOUND, ExceptionStatus.ADMIN_NOT_FOUND.getMessage());
                    }
                }
            }

            AdminTopModel admin = adminTopRepository.findById(currentAccountId)
                    .orElseThrow(() -> new GlobalException(ExceptionStatus.ADMIN_NOT_FOUND, ExceptionStatus.ADMIN_NOT_FOUND.getMessage()));
            log.info("admin : {}", admin);

            return userEntityDtoMapper.entityToDtoUserAll(admin);

        }

    }

    @Override
    public List<UserAllDto> findAll() {
        List<UserTopModel> userTopModels = userTopRepository.findAll();

        return userTopModels.stream()
                .map(userTopModel -> {
                    UserInfoModel userInfoModel = userTopModel.getUserInfoModel(); // 이미 페치된 상태
                    List<UserEducationModel> userEducationModels = userTopModel.getUserEduIds();
                    userTopModel.setRegNumberFor(encryptionUtil.decrypt(userTopModel.getRegNumberFor()));
                    userTopModel.setRegNumberLat(encryptionUtil.decrypt(userTopModel.getRegNumberLat()));
                    return UserAllDto.builder()
                            .userTopDto(userEntityDtoMapper.entityToDtoUserTop(userTopModel))
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
    public Boolean existByEmployeeId(AccountDto accountDto) {
        if (accountDto.isAdmin()) {
            return adminTopRepository.existsById(accountDto.getAccountId());
        }
        return userTopRepository.existsById(accountDto.getAccountId());
    }

    @Override
    public Optional<UserTopModel> findUserByEmployeeId(String employeeId) {
        return userTopRepository.findByEmployeeId(employeeId);
    }

    @Override
    public Optional<AdminTopModel> findAdminByEmployeeId(String employeeId) {
        return adminTopRepository.findByEmployeeId(employeeId);
    }


    @Override
    public Page<UserAllDto> findBy(String departName, String positionName, String name, Pageable pageable) {
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

//        var user = queryFactory
//                .selectFrom(qUserTop)
//                .leftJoin(qUserTop.transferIds, qTransfer)
//                .leftJoin(qTransfer.position, qPosition)
//                .leftJoin(qTransfer.department, qDepartment)
//                .where(whereClause)
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .orderBy(qUserTop.id.desc())
//                .fetch()
//                .stream()
//                .map(this::entityToDtoUserAll)
//                .toList();

        // Count query for pagination
        JPAQuery<Long> countQuery = queryFactory
                .select(qUserTop.count())
                .from(qUserTop)
                .leftJoin(qUserTop.transferIds, qTransfer)
                .leftJoin(qTransfer.position, qPosition)
                .leftJoin(qTransfer.department, qDepartment)
                .where(whereClause);

        //return PageableExecutionUtils.getPage(user, pageable, countQuery::fetchOne);
        return null;
    }

    @Override
    @Transactional
    public Boolean modifyByPassword(String oldPassword, String newPassword) {
        //if(passwordEncoder.matches(oldPassword,))
        return null;
    }

    @Override
    public ProfileDto findProfileById(Long currentAccountId, boolean isAdmin) {
        if (!isAdmin) {
            UserTopModel userTopModel = userTopRepository.findById(currentAccountId)
                    .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND, ExceptionStatus.USER_NOT_FOUND.getMessage()));

            return userEntityDtoMapper.entityToDtoUserProfile(userTopModel);
        }
        AdminTopModel adminTopModel = adminTopRepository.findById(currentAccountId)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.ADMIN_NOT_FOUND, ExceptionStatus.ADMIN_NOT_FOUND.getMessage()));

        return userEntityDtoMapper.entityToDtoUserProfile(adminTopModel);

    }

    @Override
    public UserDetailDto findDetailById(Long currentAccountId, boolean isAdmin) {
        if (!isAdmin) {
            UserTopModel userTopModel = userTopRepository.findById(currentAccountId)
                    .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND, ExceptionStatus.USER_NOT_FOUND.getMessage()));

            return userEntityDtoMapper.entityToDtoUserDetail(userTopModel);
        }
        AdminTopModel adminTopModel = adminTopRepository.findById(currentAccountId)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.ADMIN_NOT_FOUND, ExceptionStatus.ADMIN_NOT_FOUND.getMessage()));

        return userEntityDtoMapper.entityToDtoUserDetail(adminTopModel);
    }

    @Override
    @Transactional
    public UserAllDto modify(UserAllDto userAllDto, AccountDto accountDto) {
        if (accountDto.isAdmin()) {  // 관리자에 대한 로직
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Optional<AdminTopModel> updater = adminTopRepository.findById(accountDto.getAccountId());
            if ("ROLE_ADMIN_UBL_55".equals(accountDto.getRole())) {
                if (
                        !userAllDto.getUserTopDto().getRegNumberFor().isEmpty() ||
                                !userAllDto.getUserTopDto().getRegNumberLat().isEmpty()
                ) {
                    throw new GlobalException(ExceptionStatus.UNAUTHORIZED);
                }
            } else {
                Optional<AdminTopModel> admin = adminTopRepository.findByEmployeeId(userAllDto.getUserTopDto().getEmployeeId());
                Optional<UserTopModel> user = userTopRepository.findByEmployeeId(userAllDto.getUserTopDto().getEmployeeId());
                UserAllDto updatedDto;
                String oldJsonString;
                String newJsonString;
                log.info("admin: {}", admin);

                if (admin.isPresent()) {
                    log.info("update admin");
                    updatedDto = processAdminUpdate(userAllDto);
                } else {
                    log.info("update user");
                    updatedDto = processUserUpdate(userAllDto);
                }

                try {
                    if (admin.isPresent()) {
                        oldJsonString = objectMapper.writeValueAsString(userEntityDtoMapper.entityToDtoUserAll(admin.get()));
                    } else {
                        oldJsonString = objectMapper.writeValueAsString(userEntityDtoMapper.entityToDtoUserAll(user.get()));
                    }
                    newJsonString = objectMapper.writeValueAsString(updatedDto);
                    System.out.println("JSON 형태의 문자열: " + oldJsonString);
                    System.out.println("JSON 형태의 문자열: " + newJsonString);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("JSON 변환 중 오류 발생", e);
                }
                LogArchiveUpdateModel updateModel = LogArchiveUpdateModel.builder()
                        .updatedEmployeeId(userAllDto.getUserTopDto().getEmployeeId())
                        .previousValue(oldJsonString)
                        .newValue(newJsonString)
                        .updaterId(updater.get())
                        .build();
                logArchiveUpdateRepository.save(updateModel);
                return updatedDto;
            }
        }
        return processUserUpdate(userAllDto);
    }


    private UserAllDto processUserUpdate(UserAllDto userAllDto) {
        Optional<UserTopModel> userModelOpt = userTopRepository.findById(userAllDto.getUserTopDto().getId());
        if (userModelOpt.isEmpty()) {
            throw new GlobalException(ExceptionStatus.USER_NOT_FOUND);
        }

        UserTopModel userModel = userModelOpt.get();
        updateCommonFieldsForUser(userAllDto, userModel);

        UserTopModel savedUser = userTopRepository.save(userModel);
        userInfoRepository.save(userModel.getUserInfoModel());

        // 주민등록번호 복호화
        savedUser.setRegNumberFor(encryptionUtil.decrypt(savedUser.getRegNumberFor()));
        savedUser.setRegNumberLat(encryptionUtil.decrypt(savedUser.getRegNumberLat()));

        return userEntityDtoMapper.entityToDtoUserAll(savedUser);
    }

    private UserAllDto processAdminUpdate(UserAllDto userAllDto) {
        Optional<AdminTopModel> adminModelOpt = adminTopRepository.findById(userAllDto.getUserTopDto().getId());
        if (adminModelOpt.isEmpty()) {
            return null;
        }

        AdminTopModel adminModel = adminModelOpt.get();
        updateCommonFieldsForAdmin(userAllDto, adminModel);

        AdminTopModel savedAdmin = adminTopRepository.save(adminModel);

        return userEntityDtoMapper.entityToDtoUserAll(savedAdmin);
    }

    private void updateCommonFieldsForUser(UserAllDto userAllDto, UserTopModel userModel) {
        // 주민등록번호 암호화
        userAllDto.getUserTopDto().setRegNumberFor(encryptionUtil.encrypt(userAllDto.getUserTopDto().getRegNumberFor()));
        userAllDto.getUserTopDto().setRegNumberLat(encryptionUtil.encrypt(userAllDto.getUserTopDto().getRegNumberLat()));

        // 기본 정보 매핑
        utilService.mapFields(userAllDto.getUserTopDto(), userModel);
        utilService.mapFields(userAllDto.getUserInfo(), userModel.getUserInfoModel());

        // 교육 정보 업데이트
        updateEducationInfo(userAllDto.getEducation(), userModel, null);
    }

    private void updateCommonFieldsForAdmin(UserAllDto userAllDto, AdminTopModel adminModel) {
        // 기본 정보 매핑
        utilService.mapFields(userAllDto.getUserTopDto(), adminModel);
        utilService.mapFields(userAllDto.getUserInfo(), adminModel.getUserInfoModel());

        // 교육 정보 업데이트
        updateEducationInfo(userAllDto.getEducation(), null, adminModel);
    }

    private void updateEducationInfo(List<EducationDto> educationDtos, UserTopModel userModel, AdminTopModel adminModel) {
        if (educationDtos == null) {
            return;
        }

        List<UserEducationModel> educationModels;
        if (userModel != null) {
            educationModels = userModel.getUserEduIds();
        } else if (adminModel != null) {
            educationModels = adminModel.getUserEduIds();
        } else {
            throw new IllegalArgumentException("Both userModel and adminModel cannot be null");
        }

        educationModels.clear();
        for (EducationDto educationDto : educationDtos) {
            UserEducationModel newEducationModel = UserEducationModel.builder().build();
            utilService.mapFields(educationDto, newEducationModel);

            if (userModel != null) {
                newEducationModel.setUserTopModel(userModel);
            } else if (adminModel != null) {
                newEducationModel.setAdminTopModel(adminModel);
            }

            educationModels.add(newEducationModel);
        }

        userEducationRepository.saveAll(educationModels);
    }
}
