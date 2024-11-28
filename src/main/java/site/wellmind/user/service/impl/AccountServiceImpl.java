package site.wellmind.user.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.common.service.MailService;
import site.wellmind.common.service.UtilService;
import site.wellmind.log.domain.model.LogArchiveUpdateModel;
import site.wellmind.log.domain.vo.DeleteStatus;
import site.wellmind.log.event.UserDeletedEvent;
import site.wellmind.log.event.UserSavedEvent;
import site.wellmind.log.repository.LogArchiveUpdateRepository;
import site.wellmind.security.util.EncryptionUtil;
import site.wellmind.transfer.domain.model.*;
import site.wellmind.transfer.domain.vo.TransferType;
import site.wellmind.transfer.repository.TransferRepository;
import site.wellmind.user.domain.dto.*;
import site.wellmind.user.domain.model.*;
import site.wellmind.user.domain.vo.AddressVO;
import site.wellmind.user.mapper.UserDtoEntityMapper;
import site.wellmind.user.mapper.UserEntityDtoMapper;
import site.wellmind.user.repository.*;
import site.wellmind.user.service.AccountService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.stream.Stream;
import java.util.Comparator;

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
    private final TransferRepository transferRepository;
    private final UserSignificantRepository userSignificantRepository;

    private final LogArchiveUpdateRepository logArchiveUpdateRepository;

    private final UtilService utilService;
    private final EncryptionUtil encryptionUtil;
    private final MailService mailService;
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
                    .map(educationDto -> userDtoEntityMapper.dtoToEntityUserEdu(educationDto,savedUser,null))
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

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registerProfile(RegProfileDto dto, AccountDto accountDto) {
        Optional<AdminTopModel> admin = adminTopRepository.findById(accountDto.getAccountId());
        DepartmentModel departmentModel = queryFactory.selectFrom(qDepartment)
                .where(qDepartment.name.eq(dto.getDepartName()))
                .fetchOne();
        PositionModel positionModel = queryFactory.selectFrom(qPosition)
                .where(qPosition.name.eq(dto.getPositionName()))
                .fetchOne();

        if (admin.isEmpty()) {
            throw new GlobalException(ExceptionStatus.ADMIN_NOT_FOUND);
        }
        if (departmentModel == null) {
            throw new GlobalException(ExceptionStatus.INTERNAL_SERVER_ERROR, "Deparment not found");
        }
        if (positionModel == null) {
            throw new GlobalException(ExceptionStatus.INTERNAL_SERVER_ERROR, "Position not found");
        }

        try {
            eventPublisher.publishEvent(new UserSavedEvent(this, dto.getEmployeeId(), admin.get()));
            if (dto.getAuthType().equals("N")) {

                UserTopModel userTopModel = userTopRepository.save(UserTopModel.builder()
                        .email(dto.getEmail())
                        .name(dto.getName())
                        .phoneNum(dto.getPhoneNum())
                        .authType("N")
                        .employeeId(dto.getEmployeeId())
                        .role(accountRoleRepository.findById(1L).get())
                        .build());

                log.info("userTopModel: {}", userTopModel);
                transferRepository.save(TransferModel.builder()
                        .transferReason("채용")
                        .transferType(TransferType.NEW_HIRE)
                        .adminId(null)
                        .userId(userTopModel)
                        .department(departmentModel)
                        .position(positionModel)
                        .build());

                mailService.sendAccountEmail(userTopModel.getEmail(), userTopModel.getEmployeeId(), false);
            } else if (dto.getAuthType().equals("M")) {
                AdminTopModel adminTopModel = adminTopRepository.save(AdminTopModel.builder()
                        .email(dto.getEmail())
                        .name(dto.getName())
                        .phoneNum(dto.getPhoneNum())
                        .authType("M")
                        .employeeId(dto.getEmployeeId())
                        .role(accountRoleRepository.findById(1L).get())
                        .build());

                transferRepository.save(TransferModel.builder()
                        .transferReason("채용")
                        .transferType(TransferType.NEW_HIRE)
                        .adminId(adminTopModel)
                        .userId(null)
                        .department(departmentModel)
                        .position(positionModel)
                        .build());

                mailService.createAccountMail(adminTopModel.getEmail(), adminTopModel.getEmployeeId(), true);

            } else {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, "Invalid User Type : " + dto.getAuthType());
            }
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation: {}", e.getMessage());
            throw new GlobalException(ExceptionStatus.INTERNAL_SERVER_ERROR, "Failed to save UserTopModel or TransferModel due to data integrity violation");

        } catch (EntityNotFoundException e) {
            log.error("Entity not found: {}", e.getMessage());
            throw new GlobalException(ExceptionStatus.INTERNAL_SERVER_ERROR, "Required entity not found during save operation");

        } catch (Exception e) {
            throw new GlobalException(ExceptionStatus.INTERNAL_SERVER_ERROR, "Failed to save user profile data : " + dto.getEmployeeId());
        }
    }

    @Override
    @Transactional
    public void registerDetail(UserDetailDto userDetailDto, AccountDto accountDto) {
        // Step 1: UserInfoModel 저장
        if (userDetailDto == null) {
            throw new GlobalException(ExceptionStatus.INVALID_INPUT, "UserDetailDto cannot be null");
        }
        try{
            UserInfoModel userInfoModel = null;
            if (userDetailDto.getUserInfo() != null) {
                userInfoModel = userInfoRepository.save(userDtoEntityMapper.dtoToEntityUserInfo(userDetailDto.getUserInfo()));
            }

            log.info("userSignificantModel : {}",userDetailDto.getUserSignificant());
            // Step 2: UserSignificantModel 저장
            UserSignificantModel userSignificantModel = null;
            if (userDetailDto.getUserSignificant() != null) {
                userSignificantModel = userSignificantRepository.save(userDtoEntityMapper.dtoToEntitySignificant(userDetailDto.getUserSignificant()));
                log.info("userSignificantModel: {}",userSignificantModel);
            }

            if (accountDto.isAdmin()) {
                Optional<AdminTopModel> admin = adminTopRepository.findById(accountDto.getAccountId());

                AdminTopModel adminTopModel = admin.get();
                adminTopModel.setRegNumberFor(encryptionUtil.encrypt(userDetailDto.getRegNumberFor()));
                adminTopModel.setRegNumberLat(encryptionUtil.encrypt(userDetailDto.getRegNumberLat()));
                adminTopModel.setUserInfoModel(userInfoModel);
                adminTopModel.setUserSignificantModel(userSignificantModel);
                AdminTopModel savedAdmin = adminTopRepository.save(adminTopModel);

                List<UserEducationModel> educationModels = userDetailDto.getEducation().stream()
                        .map(dto -> userDtoEntityMapper.dtoToEntityUserEdu(dto, null, savedAdmin))
                        .collect(Collectors.toList());

                userEducationRepository.saveAll(educationModels);
            }
            Optional<UserTopModel> user = userTopRepository.findById(accountDto.getAccountId());
            if(user.isEmpty()){
                throw new GlobalException(ExceptionStatus.USER_NOT_FOUND);
            }
            UserTopModel userTopModel = user.get();
            userTopModel.setRegNumberFor(encryptionUtil.encrypt(userDetailDto.getRegNumberFor()));
            userTopModel.setRegNumberLat(encryptionUtil.encrypt(userDetailDto.getRegNumberLat()));
            userTopModel.setUserInfoModel(userInfoModel);
            userTopModel.setUserSignificantModel(userSignificantModel);
            UserTopModel savedUser = userTopRepository.save(userTopModel);

            List<UserEducationModel> educationModels = userDetailDto.getEducation().stream()
                    .map(dto -> userDtoEntityMapper.dtoToEntityUserEdu(dto, savedUser, null))
                    .collect(Collectors.toList());

            userEducationRepository.saveAll(educationModels);
        }catch (EntityNotFoundException e) {
            log.error("Entity not found: {}", e.getMessage());
            throw new GlobalException(ExceptionStatus.INTERNAL_SERVER_ERROR, "Required entity not found during save operation"+e);

        } catch (Exception e) {
            throw new GlobalException(ExceptionStatus.INTERNAL_SERVER_ERROR, "Failed to save user detail data : " + userDetailDto.getEmployeeId());
        }

    }

    @Override
    public List<UserAllDto> saveAll(List<UserAllDto> entities) {
        return null;
    }

    @Async
    @Override
    public void deleteById(Object ob, AccountDto accountDto) {
        UserLogRequestDto userLogRequestDto = (UserLogRequestDto) ob;
        Optional<AdminTopModel> admin = findAdminByEmployeeId(accountDto.getEmployeeId());
        Optional<UserTopModel> userTopModel = findUserByEmployeeId(userLogRequestDto.getEmployeeId());
        if (userTopModel.isPresent()) {  //삭제하려는 대상이 사용자일 경우
            UserTopModel user = userTopModel.get();
            if (!user.getDeleteFlag()) { //논리 삭제의 경우
                user.setDeleteFlag(true);
                userTopRepository.save(user);
                //saveDeleteLog(userTopModel.get(), userLogRequestDto.getDeletedReason(), DeleteStatus.CACHE, admin.get());
                eventPublisher.publishEvent(new UserDeletedEvent(this, user, userLogRequestDto.getReason(), DeleteStatus.CACHE, admin.get()));

            } else {  //완전 삭제의 경우
                //saveDeleteLog(userTopModel.get(), userLogRequestDto.getDeletedReason(), DeleteStatus.CLEAR, admin.get());
                eventPublisher.publishEvent(new UserDeletedEvent(this, user, userLogRequestDto.getReason(), DeleteStatus.CLEAR, admin.get()));
                userInfoRepository.delete(user.getUserInfoModel());
                userSignificantRepository.delete(user.getUserSignificantModel());
                userTopRepository.delete(user);
                List<UserEducationModel> userEducationModels = user.getUserEduIds();
                userEducationRepository.deleteAll(userEducationModels);
            }
        } else {  //삭제하려는 대상이 관리자일 경우
            Optional<AdminTopModel> adminTopModel = findAdminByEmployeeId(userLogRequestDto.getEmployeeId());
            if (!adminTopModel.isPresent()) {
                AdminTopModel adminUser = adminTopModel.get();
                if (!adminUser.getDeleteFlag()) { //논리 삭제의 경우
                    adminUser.setDeleteFlag(true);
                    adminTopRepository.save(adminUser);
                    // saveDeleteLog(adminTopModel.get(), userLogRequestDto.getDeletedReason(), DeleteStatus.CACHE, admin.get());
                    eventPublisher.publishEvent(new UserDeletedEvent(this, adminUser, userLogRequestDto.getReason(), DeleteStatus.CACHE, admin.get()));

                } else {  //완전 삭제인 경우
                    //saveDeleteLog(adminTopModel.get(), userLogRequestDto.getDeletedReason(), DeleteStatus.CLEAR, admin.get());
                    eventPublisher.publishEvent(new UserDeletedEvent(this, adminUser, userLogRequestDto.getReason(), DeleteStatus.CLEAR, admin.get()));

                    userInfoRepository.delete(adminUser.getUserInfoModel());
                    userSignificantRepository.delete(adminUser.getUserSignificantModel());
                    adminTopRepository.delete(adminUser);
                    List<UserEducationModel> userEducationModels = adminUser.getUserEduIds();
                    userEducationRepository.deleteAll(userEducationModels);
                }
            } else {
                throw new GlobalException(ExceptionStatus.ACCOUNT_NOT_FOUND);
            }
        }
    }


    @Override
    @Transactional
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
    public Page<ProfileDto> findBy(String departName, String positionName, String name, Pageable pageable) {
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

        List<ProfileDto> userProfiles = queryFactory
                .select(Projections.constructor(
                        ProfileDto.class,
                        qUserTop.id.as("id"),
                        qUserTop.email.as("email"),
                        qUserTop.name.as("name"),
                        qUserTop.phoneNum.as("phoneNum"),
                        qUserTop.authType.as("authType"),
                        qUserTop.deleteFlag.as("deleteFlag"),
                        qUserTop.userInfoModel.photo.as("photo"),
                        qUserTop.userInfoModel.address.as("address"),
                        qDepartment.name.as("departName"),
                        qPosition.name.as("positionName")
                ))
                .from(qUserTop)
                .leftJoin(qUserTop.transferEmployeeIds, qTransfer)
                .leftJoin(qTransfer.position, qPosition)
                .leftJoin(qTransfer.department, qDepartment)
                .where(whereClause)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qUserTop.id.desc())
                .fetch();

        List<ProfileDto> adminProfiles = queryFactory
                .select(Projections.constructor(
                        ProfileDto.class,
                        qAdminTop.id.as("id"),
                        qAdminTop.email.as("email"),
                        qAdminTop.name.as("name"),
                        qAdminTop.phoneNum.as("phoneNum"),
                        qAdminTop.authType.as("authType"),
                        qAdminTop.deleteFlag.as("deleteFlag"),
                        qAdminTop.userInfoModel.photo.as("photo"),
                        qAdminTop.userInfoModel.address.as("address"),
                        qDepartment.name.as("departName"),
                        qPosition.name.as("positionName")
                ))
                .from(qAdminTop)
                .leftJoin(qAdminTop.transferIds, qTransfer)
                .leftJoin(qTransfer.position, qPosition)
                .leftJoin(qTransfer.department, qDepartment)
                .where(whereClause)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qAdminTop.id.desc())
                .fetch();

        List<ProfileDto> allProfiles = Stream.concat(userProfiles.stream(), adminProfiles.stream())
                .sorted(Comparator.comparing(ProfileDto::getId).reversed()) // 정렬 기준: ID 역순
                .collect(Collectors.toList());
        // 전체 데이터 수
        int totalSize = allProfiles.size();
        // 페이징 처리
        int start = (int) pageable.getOffset();

        if (start >= totalSize) { // 시작 인덱스가 전체 크기보다 크면 빈 리스트 반환
            return new PageImpl<>(Collections.emptyList(), pageable, totalSize);
        }

        int end = Math.min((start + pageable.getPageSize()), totalSize);
        List<ProfileDto> paginatedProfiles = allProfiles.subList(start, end);
        return new PageImpl<>(paginatedProfiles, pageable, totalSize);
    }

    @Override
    @Transactional
    public Boolean modifyByPassword(String oldPassword, String newPassword) {
        //if(passwordEncoder.matches(oldPassword,))
        return null;
    }

    @Override
    @Transactional
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
    @Transactional
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
