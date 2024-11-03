package site.wellmind.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.security.util.EncryptionUtil;
import site.wellmind.user.domain.dto.ProfileDto;
import site.wellmind.user.domain.dto.UserDto;
import site.wellmind.user.domain.model.UserEducationModel;
import site.wellmind.user.domain.model.UserInfoModel;
import site.wellmind.user.domain.model.UserTopModel;
import site.wellmind.user.exception.UserException;
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
    @Override
    public UserDto save(UserDto dto) {
        //validate user request
        validateUserDto(dto);

        //password encoding
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));

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

    }

    @Override
    public UserDto update(UserDto userDto) {
        return null;
    }

    @Override
    public UserDto findById(Long id) {
        return null;
    }

    @Override
    public List<UserDto> findAll() {
        return null;
    }

    @Override
    public boolean existById(Long id) {
        return false;
    }

    @Override
    public Long count() {
        return null;
    }

    @Override
    public Boolean existByEmployeeId(String employeeId) {
        return userTopRepository.existsByEmployeeId(employeeId);
    }

    @Override
    public ProfileDto login(UserDto dto) {
        return null;
    }

    private void validateUserDto(UserDto dto) {
        if (dto.getEmployeeId().isEmpty() || dto.getPassword().isEmpty()) {
            throw new UserException(ExceptionStatus.INVALID_INPUT, "employeeeId or password cannot be empty");
        }
    }

}
