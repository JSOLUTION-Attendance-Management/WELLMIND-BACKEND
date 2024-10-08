package site.wellmind.user.service;
import site.wellmind.common.service.CommandService;
import site.wellmind.common.service.QueryService;
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
                .email(dto.getEmail())
                .password(dto.getPassword())
                .name(dto.getName())
                .tel(dto.getTel())
                .deleteFlag(dto.getDeleteFlag())
                .authType(dto.getAuthType())
                .authUserLevelCodeId(dto.getAuthUserLevelCodeId())
                .build();
    }

    default UserDto entityToDto(UserTopModel model){
        return UserDto.builder()
                .email(model.getEmail())
                .password(model.getPassword())
                .name(model.getName())
                .tel(model.getTel())
                .deleteFlag(model.getDeleteFlag())
                .authType(model.getAuthType())
                .authUserLevelCodeId(model.getAuthUserLevelCodeId())
                .regDate(model.getRegDate())
                .modDate(model.getModDate())
                .build();
    }
}

