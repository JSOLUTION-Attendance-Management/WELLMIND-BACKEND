package site.wellmind.attend.domain.vo;

import site.wellmind.attend.converter.ReportStatusConverter;
import site.wellmind.attend.domain.model.AttendReportModel;

/**
 * ReportAttendanceStatus
 * <p>ai 근태 유형 분석 결과 enum</p>
 * @since 2024-11-12
 * @version 1.0
 * @see AttendReportModel
 * @see ReportStatusConverter
 */
public enum ReportStatus {
    EL,OT,LA,LL,BT  //조퇴, 외출, 지각, 야근, 출장
}
