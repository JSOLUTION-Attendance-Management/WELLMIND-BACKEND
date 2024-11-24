package site.wellmind.attend.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import site.wellmind.attend.domain.model.AttendReportModel;
import site.wellmind.attend.domain.vo.ReportStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ReportStatus
 * <p>List<ReportStatus>를 String으로 변환</p>
 * @since 2024-11-12
 * @version 1.0
 * @see AttendReportModel
 * @see ReportStatus
 */
@Converter
public class ReportStatusConverter implements AttributeConverter<List<ReportStatus>,String> {

    @Override
    public String convertToDatabaseColumn(List<ReportStatus> reportStatuses) {
        return reportStatuses!=null ? reportStatuses.stream()
                .map(Enum::name)
                .collect(Collectors.joining(",")):null;
    }

    @Override
    public List<ReportStatus> convertToEntityAttribute(String s) {
        return s != null ? Arrays.stream(s.split(","))
                .map(ReportStatus::valueOf)
                .collect(Collectors.toList()) : null;
    }
}
