package site.wellmind.attend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.wellmind.attend.domain.dto.AttendDto;
import site.wellmind.attend.domain.model.*;
import site.wellmind.user.domain.dto.AccountDto;

import java.util.List;

/**
 * AttendService
 * <p>User Service Interface</p>
 *
 * @version 1.0
 * @see AttendDto
 * @since 2024-11-20
 */
public interface AttendService {
    default AttendDto entityToDtoAttendRecord(AttendRecordModel model) {
        return AttendDto.builder()
                .attendStatus(model.getAttendStatus())
                .regDate(model.getRegDate())
                .build();
    }

    Page<AttendDto> findBy(String employeeId, AccountDto accountDto, Pageable pageable, Integer recentCount);

    List<AttendDto> findRecentAttendances(String employeeId, AccountDto accountDto, Integer recentCount);
}