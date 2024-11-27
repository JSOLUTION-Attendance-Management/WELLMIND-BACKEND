package site.wellmind.log.service;

import site.wellmind.common.service.CommandService;
import site.wellmind.common.service.QueryService;
import site.wellmind.log.domain.dto.LogDeleteDto;
import site.wellmind.log.domain.dto.LogSaveDto;
import site.wellmind.user.domain.dto.AccountDto;
import site.wellmind.user.domain.dto.UserLogRequestDto;

public interface LogSaveService extends CommandService<LogSaveDto>, QueryService<LogSaveDto> {
}
