package site.wellmind.attend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.wellmind.attend.domain.dto.AttendDto;
import site.wellmind.attend.domain.dto.SimpleAttendDto;
import site.wellmind.attend.domain.dto.BaseAttendDto;
import site.wellmind.attend.domain.dto.RecentAttendDto;
import site.wellmind.attend.domain.model.*;
import site.wellmind.user.domain.dto.AccountDto;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * AttendService
 * <p>Attend Service Interface</p>
 *
 * @version 1.0
 * @see AttendDto
 * @since 2024-11-20
 */
public interface AttendService {
    default AttendDto entityToDtoAttendRecord(AttendRecordModel model) {
        LocalDateTime regDate = model.getRegDate();
        return AttendDto.builder()
                .attendStatus(model.getAttendStatus())
                .date(regDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .time(regDate.format(DateTimeFormatter.ofPattern("HH:mm")))
                .timeSec(regDate.format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                .build();
    }

    default SimpleAttendDto entityToDtoSimpleAttendRecord(AttendRecordModel model) {
        LocalDateTime regDate = model.getRegDate();
        return SimpleAttendDto.builder()
                .attendStatus(model.getAttendStatus())
                .date(regDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .build();
    }

    default RecentAttendDto entityToDtoRecentAttendRecord(AttendRecordModel model) {
        LocalDateTime regDate = model.getRegDate();
        return RecentAttendDto.builder()
                .date(regDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .time(regDate.format(DateTimeFormatter.ofPattern("HH:mm")))
                .build();
    }

    Page<BaseAttendDto> findBy(String employeeId, AccountDto accountDto, Pageable pageable, LocalDate startDate, LocalDate endDate);

    List<RecentAttendDto> findRecentAttendances(AccountDto accountDto, Integer recentCount);

    Optional<UserTopModel> findUserByEmployeeId(String employeeId);

    Optional<AdminTopModel> findAdminByEmployeeId(String employeeId);
}