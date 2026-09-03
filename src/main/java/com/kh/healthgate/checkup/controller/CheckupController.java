package com.kh.healthgate.checkup.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.healthgate.checkup.model.dto.CheckupExcelUploadResponse;
import com.kh.healthgate.checkup.model.dto.CheckupStatisticsResponse;
import com.kh.healthgate.checkup.model.dto.CheckupTargetResponse;
import com.kh.healthgate.checkup.model.dto.ManualReminderRequest;
import com.kh.healthgate.checkup.model.dto.ReminderResponse;
import com.kh.healthgate.checkup.model.dto.ReminderSettingRequest;
import com.kh.healthgate.checkup.model.dto.ReminderSettingResponse;
import com.kh.healthgate.checkup.model.service.CheckupService;
import com.kh.healthgate.checkup.model.service.CheckupReminderLogService;

/**
 * 건강검진 관련 API 요청을 처리하는 Controller
 */
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/checkups")
public class CheckupController {

    @Autowired
    private CheckupService checkupService;
    @Autowired
    private CheckupReminderLogService checkupReminderLogService;

    /**
     * 연도별 건강검진 완료율 통계 조회
     *
     * GET /healthgate/checkups/statistics?year=2026
     */
    @GetMapping("/statistics")
    public ResponseEntity<CheckupStatisticsResponse>
            getCheckupStatistics(
                    @RequestParam("year") Short checkupYear) {

        CheckupStatisticsResponse statistics =
                checkupService.getCheckupStatistics(checkupYear);

        return ResponseEntity.ok(statistics);
    }

    /**
     * 연도별 건강검진 대상자 목록 조회
     *
     * GET /healthgate/checkups/targets?year=2026
     */
    @GetMapping("/targets")
    public ResponseEntity<List<CheckupTargetResponse>>
            getCheckupTargets(
                    @RequestParam("year") Short checkupYear) {

        List<CheckupTargetResponse> targetList =
                checkupService.getCheckupTargets(checkupYear);

        return ResponseEntity.ok(targetList);
    }

    /**
     * 건강검진 결과 Excel 파일 업로드
     *
     * 요청 주소:
     * POST /healthgate/checkups/excel-upload
     *
     * 요청 형식:
     * multipart/form-data
     *
     * 파일 필드명:
     * file
     */
    @PostMapping(
        value = "/excel-upload",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<CheckupExcelUploadResponse>
            uploadCheckupExcel(
                    @RequestParam("file") MultipartFile file) {

        CheckupExcelUploadResponse response =
                checkupService.uploadCheckupExcel(file);

        return ResponseEntity.ok(response);
    }

    /**
     * 건강검진 수동 알림 발송
     *
     * 현재는 실제 SMS·이메일 서비스 연동 전이므로
     * 발송 정보를 성공 상태로 알림 이력 테이블에 저장한다.
     *
     * POST /healthgate/checkups/reminders/manual
     */
    @PostMapping("/reminders/manual")
    public ResponseEntity<ReminderResponse>
            sendManualReminder(
                    @RequestBody ManualReminderRequest request) {

        ReminderResponse response =
                checkupService.sendManualReminder(request);

        return ResponseEntity.ok(response);
    }

    /**
     * 자동 알림 설정 전체 목록 조회
     *
     * GET /healthgate/checkups/reminder-settings
     */
    @GetMapping("/reminder-settings")
    public ResponseEntity<List<ReminderSettingResponse>>
            getReminderSettings() {

        List<ReminderSettingResponse> settingList =
                checkupService.getReminderSettings();

        return ResponseEntity.ok(settingList);
    }

    /**
     * 자동 알림 설정 수정
     *
     * PUT /healthgate/checkups/reminder-settings/{settingId}
     */
    @PutMapping("/reminder-settings/{settingId}")
    public ResponseEntity<ReminderSettingResponse>
            updateReminderSetting(
                    @PathVariable("settingId") Long settingId,
                    @RequestBody ReminderSettingRequest request) {

        ReminderSettingResponse response =
                checkupService.updateReminderSetting(
                        settingId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    /**
     * 건강검진 알림 발송 이력 전체 조회
     *
     * GET /healthgate/checkups/reminders/history
     */
    @GetMapping("/reminders/history")
    public ResponseEntity<List<ReminderResponse>>
            getReminderHistory() {

        List<ReminderResponse> historyList =
                checkupService.getReminderHistory();

        return ResponseEntity.ok(historyList);
    }
    
    /**
     * 건강검진 알림 발송 이력 Excel 다운로드
     *
     * GET /healthgate/checkups/reminders/history/excel
     */
    @GetMapping("/reminders/history/excel")
    public ResponseEntity<byte[]> downloadReminderHistoryExcel(
            @RequestParam(
                value = "channel",
                required = false
            ) String channel,

            @RequestParam(
                value = "manual",
                required = false
            ) Boolean manual) {

        byte[] excelData =
                checkupReminderLogService
                        .downloadReminderHistoryExcel(
                                channel,
                                manual
                        );

        String downloadDate =
                LocalDate.now().format(
                        DateTimeFormatter.BASIC_ISO_DATE
                );

        String fileName =
                "건강검진_알림발송이력_"
                + downloadDate
                + ".xlsx";

        ContentDisposition contentDisposition =
                ContentDisposition
                        .attachment()
                        .filename(
                                fileName,
                                StandardCharsets.UTF_8
                        )
                        .build();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        contentDisposition.toString()
                )
                .contentType(
                        MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .contentLength(excelData.length)
                .body(excelData);
    }
    
}
