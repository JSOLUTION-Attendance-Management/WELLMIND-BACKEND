package site.wellmind.log.service;

import site.wellmind.common.service.CommandService;
import site.wellmind.common.service.QueryService;
import site.wellmind.log.domain.dto.LogViewDto;

/**
 * AccountService
 * <p>User Service Interface</p>
 * @since 2024-10-08
 * @version 1.0
 * @see QueryService
 * @see CommandService
 * @see LogViewDto
 */
public interface LogViewService extends CommandService<LogViewDto>, QueryService<LogViewDto> {
}
