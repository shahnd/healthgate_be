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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * 건강검진 관련 API 요청을 처리하는 Controller
 */
@Tag(
    name = "건강검진",
    description = "건강검진 대상자, 완료율, 엑셀 및 알림 관리 API"
)
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
    @Operation(
        summary = "건강검진 완료율 조회",
        description = "선택한 연도의 전체 대상자, 완료자, 미완료자 수와 완료율을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "완료율 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 검진 연도"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/statistics")
    public ResponseEntity<CheckupStatisticsResponse>
            getCheckupStatistics(
                    @Parameter(
                        description = "조회할 검진 연도",
                        example = "2026",
                        required = true
                    )
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
    @Operation(
        summary = "건강검진 대상자 목록 조회",
        description = "선택한 연도의 건강검진 대상자와 검진 완료 상태를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "대상자 목록 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 검진 연도"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/targets")
    public ResponseEntity<List<CheckupTargetResponse>>
            getCheckupTargets(
                    @Parameter(
                        description = "조회할 검진 연도",
                        example = "2026",
                        required = true
                    )
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
    @Operation(
        summary = "건강검진 결과 엑셀 업로드",
        description = "건강검진 결과 엑셀 파일을 업로드합니다. 기존 직원·연도 기록은 갱신하고, 없는 기록은 새로 등록합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "엑셀 처리 완료"),
        @ApiResponse(responseCode = "400", description = "파일 형식 또는 데이터 오류"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping(
        value = "/excel-upload",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<CheckupExcelUploadResponse>
            uploadCheckupExcel(
                    @Parameter(
                        description = "업로드할 건강검진 결과 엑셀 파일",
                        required = true
                    )
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
    @Operation(
        summary = "건강검진 수동 알림 발송",
        description = "선택한 검진 대상자에게 이메일 또는 SMS 알림을 발송하고 발송 이력을 저장합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "알림 발송 처리 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 알림 요청"),
        @ApiResponse(responseCode = "404", description = "검진 대상자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
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
    @Operation(
        summary = "자동 알림 설정 조회",
        description = "등록된 건강검진 자동 알림 설정을 모두 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "자동 알림 설정 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
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
    @Operation(
        summary = "자동 알림 설정 수정",
        description = "설정 ID에 해당하는 자동 알림의 메시지, 실행 일정 및 활성화 상태를 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "자동 알림 설정 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 알림 설정 요청"),
        @ApiResponse(responseCode = "404", description = "자동 알림 설정을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PutMapping("/reminder-settings/{settingId}")
    public ResponseEntity<ReminderSettingResponse>
            updateReminderSetting(
                    @Parameter(
                        description = "수정할 자동 알림 설정 ID",
                        example = "1",
                        required = true
                    )
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
    @Operation(
        summary = "알림 발송 이력 조회",
        description = "건강검진 알림 발송 이력을 최신순으로 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "알림 발송 이력 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
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
    @Operation(
        summary = "알림 발송 이력 엑셀 다운로드",
        description = "채널과 수동 발송 여부를 기준으로 발송 이력을 필터링하여 엑셀 파일로 다운로드합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "알림 발송 이력 엑셀 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 필터 조건"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/reminders/history/excel")
    public ResponseEntity<byte[]> downloadReminderHistoryExcel(
            @Parameter(
                description = "발송 채널",
                example = "EMAIL"
            )
            @RequestParam(
                value = "channel",
                required = false
            ) String channel,

            @Parameter(
                description = "수동 발송 여부",
                example = "true"
            )
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
