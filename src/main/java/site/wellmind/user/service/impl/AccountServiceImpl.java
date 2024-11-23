package site.wellmind.user.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.common.service.UtilService;
import site.wellmind.log.repository.LogArchiveUpdateRepository;
import site.wellmind.security.util.EncryptionUtil;
import site.wellmind.transfer.domain.model.QDepartmentModel;
import site.wellmind.transfer.domain.model.QPositionModel;
import site.wellmind.transfer.domain.model.QTransferModel;
import site.wellmind.user.domain.dto.*;
import site.wellmind.user.domain.model.*;
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

    private final UtilService utilService;
    private final EncryptionUtil encryptionUtil;
    private final PasswordEncoder passwordEncoder;

    private final JPAQueryFactory queryFactory;
    private final QUserTopModel qUserTop = QUserTopModel.userTopModel;
    private final QAdminTopModel qAdminTop = QAdminTopModel.adminTopModel;
    private final QTransferModel qTransfer = QTransferModel.transferModel;
    private final QPositionModel qPosition = QPositionModel.positionModel;
    private final QDepartmentModel qDepartment = QDepartmentModel.departmentModel;

    @Override
    @Transactional
    public Object save(UserDto dto) {

        try {

            UserInfoModel userInfoModel = userInfoRepository.save(dtoToEntityUserInfo(dto.getUserInfo()));

            AccountRoleModel accountRoleModel = accountRoleRepository.findByRoleId("UGL_11");
            //UserTopModel savedUser = userTopRepository.save(dtoToEntity(dto));
            dto.getUserTopDto().setRegNumberFor(encryptionUtil.encrypt(dto.getUserTopDto().getRegNumberFor()));
            dto.getUserTopDto().setRegNumberLat(encryptionUtil.encrypt(dto.getUserTopDto().getRegNumberLat()));
            UserTopModel savedUser = userTopRepository.save(dtoToEntityUserAll(dto,userInfoModel,accountRoleModel));

            List<UserEducationModel> educationEntities = dto.getEducation().stream()
                    .map(educationDto -> dtoToEntityUserEdu(educationDto, savedUser))
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
    public List<UserDto> saveAll(List<UserDto> entities) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        if (existById(id)) {
            userTopRepository.deleteById(id);
        } else {
            throw new GlobalException(ExceptionStatus.ACCOUNT_NOT_FOUND, "USERTOP_IDX not found");
        }

    }

    @Override
    @Transactional
    public UserDto modify(UserDto userDto,AccountDto accountDto) {
        if(!accountDto.isAdmin()){ //사용자
            Optional<UserTopModel> userModel=userTopRepository.findById(accountDto.getAccountId());
            if(userModel.isEmpty()){
                throw new GlobalException(ExceptionStatus.USER_NOT_FOUND);
            }
            userDto.getUserTopDto().setRegNumberFor(encryptionUtil.encrypt(userDto.getUserTopDto().getRegNumberFor()));
            userDto.getUserTopDto().setRegNumberLat(encryptionUtil.encrypt(userDto.getUserTopDto().getRegNumberLat()));

            UserInfoModel userInfoModel=userModel.get().getUserInfoModel();
            utilService.mapFields(userDto.getUserTopDto(), userModel.get());
            utilService.mapFields(userDto.getUserInfo(), userInfoModel);

            List<UserEducationModel> userEducationModel=userModel.get().getUserEduIds();
            List<EducationDto> educationDtos=userDto.getEducation();
            if(educationDtos!=null){
                userEducationModel.clear();
                for(EducationDto educationDto:educationDtos){
                    UserEducationModel newEducationModel = UserEducationModel.builder().build();
                    utilService.mapFields(educationDto,newEducationModel);
                    newEducationModel.setUserTopModel(userModel.get());
                    userEducationModel.add(newEducationModel);
                }
                userEducationRepository.saveAll(userEducationModel);
            }

            UserTopModel savedUser=userTopRepository.save(userModel.get());
            userInfoRepository.save(userInfoModel);

            savedUser.setRegNumberFor(encryptionUtil.decrypt(savedUser.getRegNumberFor()));
            savedUser.setRegNumberLat(encryptionUtil.decrypt(savedUser.getRegNumberLat()));
            return entityToDtoUserAll(savedUser);
        }else{ //관리자

        }
        return null;
    }

    @Override
    public Object findById(String employeeId, AccountDto accountDto) {
        Long currentAccountId=accountDto.getAccountId();

        if (!accountDto.isAdmin()) {  //사용자
            UserTopModel userTopModel = userTopRepository.findById(currentAccountId)
                    .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND, ExceptionStatus.USER_NOT_FOUND.getMessage()));
            return entityToDtoUserAll(userTopModel);
        } else { //관리자

            if(employeeId!=null){
                Optional<UserTopModel> user=findUserByEmployeeId(employeeId);
                log.info("user : {}",user);

                if(!user.isEmpty()){
                    if(accountDto.getRole().equals("ROLE_ADMIN_UBL_55")){
                        return entityToDtoUserProfile(user.get());
                    }else if(accountDto.getRole().equals("ROLE_ADMIN_UBL_66")){
                        return entityToDtoUserAll(user.get());
                    }else{
                        throw new GlobalException(ExceptionStatus.UNAUTHORIZED, ExceptionStatus.UNAUTHORIZED.getMessage());
                    }
                }
                Optional<AdminTopModel> admin=findAdminByEmployeeId(employeeId);
                log.info("admin : {}",admin);

                if(!admin.isEmpty()){
                    if(accountDto.getRole().equals("ROLE_ADMIN_UBL_55")){
                        return entityToDtoUserProfile(admin.get());
                    }else if(accountDto.getRole().equals("ROLE_ADMIN_UBL_66")){
                        return entityToDtoUserAll(admin.get());
                    }else{
                        throw new GlobalException(ExceptionStatus.ADMIN_NOT_FOUND, ExceptionStatus.ADMIN_NOT_FOUND.getMessage());
                    }
                }
             }

            AdminTopModel admin = adminTopRepository.findById(currentAccountId)
                    .orElseThrow(() -> new GlobalException(ExceptionStatus.ADMIN_NOT_FOUND, ExceptionStatus.ADMIN_NOT_FOUND.getMessage()));
            log.info("admin : {}",admin);

            return entityToDtoUserAll(admin);

        }

    }

    @Override
    public List<UserDto> findAll() {
        List<UserTopModel> userTopModels = userTopRepository.findAll();

        return userTopModels.stream()
                .map(userTopModel -> {
                    UserInfoModel userInfoModel = userTopModel.getUserInfoModel(); // 이미 페치된 상태
                    List<UserEducationModel> userEducationModels = userTopModel.getUserEduIds();
                    userTopModel.setRegNumberFor(encryptionUtil.decrypt(userTopModel.getRegNumberFor()));
                    userTopModel.setRegNumberLat(encryptionUtil.decrypt(userTopModel.getRegNumberLat()));
                    return UserDto.builder()
                            .userTopDto(entityToDtoUserTop(userTopModel))
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
    public Optional<UserTopModel> findUserByEmployeeId(String employeeId) {
        return userTopRepository.findByEmployeeId(employeeId);
    }

    @Override
    public Optional<AdminTopModel> findAdminByEmployeeId(String employeeId) {
        return adminTopRepository.findByEmployeeId(employeeId);
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
        if(!isAdmin){
            UserTopModel userTopModel = userTopRepository.findById(currentAccountId)
                    .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND, ExceptionStatus.USER_NOT_FOUND.getMessage()));

            return entityToDtoUserProfile(userTopModel);
        }
        AdminTopModel adminTopModel=adminTopRepository.findById(currentAccountId)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.ADMIN_NOT_FOUND, ExceptionStatus.ADMIN_NOT_FOUND.getMessage()));

        return entityToDtoUserProfile(adminTopModel);

    }

}
