package site.wellmind.log.domain.vo;

import site.wellmind.log.converter.AttendanceStatusConverter;
import site.wellmind.log.domain.model.LogArchiveReportModel;

/**
 * ReportAttendanceStatus
 * <p>ai 근태 유형 분석 결과 enum</p>
 * @since 2024-11-12
 * @version 1.0
 * @see LogArchiveReportModel
 * @see AttendanceStatusConverter
 */
public enum ReportAttendanceStatus {
    EL,OT,LA,LL,BT  //조퇴, 외출, 지각, 야근, 출장
}
