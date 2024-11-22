package site.wellmind.attend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.wellmind.common.service.CommandService;
import site.wellmind.common.service.QueryService;
import site.wellmind.attend.domain.dto.AttendDto;
import site.wellmind.attend.domain.model.*;

/**
 * AttendService
 * <p>User Service Interface</p>
 *
 * @version 1.0
 * @see QueryService
 * @see CommandService
 * @see AttendDto
 * @since 2024-11-20
 */
public interface AttendService extends CommandService<AttendDto>, QueryService<AttendDto> {
    default AttendDto entityToDtoAttendRecord(AttendRecordModel model) {
        return AttendDto.builder()
                .attendStatus(model.getAttendStatus())
                .regDate(model.getRegDate())
                .build();
    }

    Page<AttendDto> findBy(String userId, Pageable pageable);
}