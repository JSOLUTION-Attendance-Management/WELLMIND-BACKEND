package site.wellmind.user.service;
import site.wellmind.common.service.CommandService;
import site.wellmind.common.service.QueryService;
import site.wellmind.user.domain.dto.ProfileDto;
import site.wellmind.user.domain.dto.UserDto;
import site.wellmind.user.domain.model.UserTopModel;

/**
 * UserService
 * <p>User Service Interface</p>
 * @since 2024-10-08
 * @version 1.0
 * @see QueryService
 * @see CommandService
 * @see UserDto
 */
public interface UserService extends CommandService<UserDto>, QueryService<UserDto> {
    default UserTopModel dtoToEntity(UserTopModel dto){
        return UserTopModel.builder()
                .employeeId(dto.getEmployeeId())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .name(dto.getName())
                .phoneNum(dto.getPhoneNum())
                .regNumberFor(dto.getRegNumberFor())
                .regNumberLat(dto.getRegNumberLat())
                .deleteFlag(dto.getDeleteFlag())
                .authType(dto.getAuthType())
                .authUserLevelCodeId(dto.getAuthUserLevelCodeId())
                .build();
    }

    default UserDto entityToDto(UserTopModel model){
        return UserDto.builder()
                .id(model.getId())
                .employeeId(model.getEmployeeId())
                .email(model.getEmail())
                .password(model.getPassword())
                .name(model.getName())
                .phoneNum(model.getPhoneNum())
                .regNumberFor(model.getRegNumberFor())
                .regNumberLat(model.getRegNumberLat())
                .deleteFlag(model.getDeleteFlag())
                .authType(model.getAuthType())
                .authUserLevelCodeId(model.getAuthUserLevelCodeId())
                .regDate(model.getRegDate())
                .modDate(model.getModDate())
                .build();
    }

    Boolean existByEmployeeId(String employeeId);

    ProfileDto login(UserDto dto);
}

