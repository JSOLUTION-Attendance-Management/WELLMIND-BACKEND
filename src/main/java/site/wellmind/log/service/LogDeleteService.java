package site.wellmind.log.service;

import site.wellmind.common.service.CommandService;
import site.wellmind.common.service.QueryService;
import site.wellmind.log.domain.dto.LogDeleteDto;
import site.wellmind.user.domain.dto.AccountDto;
import site.wellmind.user.domain.dto.UserLogRequestDto;

public interface LogDeleteService extends CommandService<LogDeleteDto>, QueryService<LogDeleteDto> {
    void recovery(UserLogRequestDto userLogRequestDto, AccountDto accountDto);

}
