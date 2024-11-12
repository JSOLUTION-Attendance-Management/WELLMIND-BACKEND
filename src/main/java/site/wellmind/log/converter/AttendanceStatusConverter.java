package site.wellmind.log.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import site.wellmind.log.domain.model.LogArchiveReportModel;
import site.wellmind.log.domain.vo.ReportAttendanceStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ReportAttendanceStatus
 * <p>List<AttendanceStatus>를 String으로 변환</p>
 * @since 2024-11-12
 * @version 1.0
 * @see LogArchiveReportModel
 * @see ReportAttendanceStatus
 */
@Converter
public class AttendanceStatusConverter implements AttributeConverter<List<ReportAttendanceStatus>,String> {

    @Override
    public String convertToDatabaseColumn(List<ReportAttendanceStatus> reportAttendanceStatuses) {
        return reportAttendanceStatuses!=null ? reportAttendanceStatuses.stream()
                .map(Enum::name)
                .collect(Collectors.joining(",")):null;
    }

    @Override
    public List<ReportAttendanceStatus> convertToEntityAttribute(String s) {
        return s != null ? Arrays.stream(s.split(","))
                .map(ReportAttendanceStatus::valueOf)
                .collect(Collectors.toList()) : null;
    }
}
