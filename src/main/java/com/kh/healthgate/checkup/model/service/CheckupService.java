package com.kh.healthgate.checkup.model.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.healthgate.checkup.model.dao.CheckupDao;
import com.kh.healthgate.checkup.model.dao.CheckupReminderDao;
import com.kh.healthgate.checkup.model.dao.CheckupReminderSettingDao;

import com.kh.healthgate.checkup.model.dto.CheckupExcelUploadResponse;
import com.kh.healthgate.checkup.model.dto.CheckupStatisticsResponse;
import com.kh.healthgate.checkup.model.dto.CheckupTargetResponse;
import com.kh.healthgate.checkup.model.dto.ManualReminderRequest;
import com.kh.healthgate.checkup.model.dto.ReminderResponse;
import com.kh.healthgate.checkup.model.dto.ReminderSettingRequest;
import com.kh.healthgate.checkup.model.dto.ReminderSettingResponse;

import com.kh.healthgate.checkup.model.vo.Checkup;
import com.kh.healthgate.checkup.model.vo.CheckupReminder;
import com.kh.healthgate.checkup.model.vo.CheckupReminderSetting;

import com.kh.healthgate.employee.model.dao.EmployeeDao;
import com.kh.healthgate.employee.model.vo.Employee;

/**
 * 건강검진 관련 비즈니스 로직을 담당하는 Service
 */
@Service
public class CheckupService {

    @Autowired
    private CheckupDao checkupDao;

    @Autowired
    private CheckupReminderDao checkupReminderDao;

    @Autowired
    private CheckupReminderSettingDao checkupReminderSettingDao;

    @Autowired
    private EmployeeDao employeeDao;

    /**
     * 지정한 연도의 건강검진 완료율 통계를 조회한다.
     *
     * @param checkupYear 조회할 검진 대상 연도
     * @return 전체·완료·미완료 인원 및 완료율
     */
    public CheckupStatisticsResponse getCheckupStatistics(
            Short checkupYear) {

        long totalCount =
                checkupDao.countByCheckupYear(checkupYear);

        long completedCount =
                checkupDao.countByCheckupYearAndCheckupDateIsNotNull(
                        checkupYear
                );

        long incompleteCount = totalCount - completedCount;

        double completionRate = totalCount == 0
                ? 0.0
                : (double) completedCount / totalCount * 100;

        completionRate =
                Math.round(completionRate * 10.0) / 10.0;

        return new CheckupStatisticsResponse(
                checkupYear,
                totalCount,
                completedCount,
                incompleteCount,
                completionRate
        );
    }

    /**
     * 지정한 연도의 건강검진 대상자 목록을 조회한다.
     *
     * @param checkupYear 조회할 검진 대상 연도
     * @return 건강검진 대상자 목록
     */
    @Transactional(readOnly = true)
    public List<CheckupTargetResponse> getCheckupTargets(
            Short checkupYear) {

        List<Checkup> checkupList =
                checkupDao.findByCheckupYearOrderByCheckupIdAsc(
                        checkupYear
                );

        return checkupList.stream()
                .map(checkup -> new CheckupTargetResponse(
                        checkup.getCheckupId(),
                        checkup.getCheckupYear(),
                        checkup.getCheckupDate(),
                        checkup.getCheckupSummary(),
                        checkup.getCheckupDate() != null,
                        checkup.getEmployee().getId(),
                        checkup.getEmployee().getEmployeeNumber(),
                        checkup.getEmployee().getName()
                ))
                .toList();
    }

    /**
     * 건강검진 결과 Excel 파일을 업로드한다.
     *
     * Excel 열 순서:
     * A열 사번
     * B열 이름
     * C열 검진연도
     * D열 검진일
     * E열 검진요약
     *
     * 첫 번째 행은 제목 행이므로 처리하지 않는다.
     */
    @Transactional
    public CheckupExcelUploadResponse uploadCheckupExcel(
            MultipartFile file) {

        validateExcelFile(file);

        int totalCount = 0;
        int successCount = 0;
        List<String> errors = new ArrayList<>();

        try (
            Workbook workbook =
                    WorkbookFactory.create(file.getInputStream())
        ) {
            Sheet sheet = workbook.getSheetAt(0);

            // 0번 행은 제목 행이므로 1번 행부터 처리한다.
            for (int rowIndex = 1;
                    rowIndex <= sheet.getLastRowNum();
                    rowIndex++) {

                Row row = sheet.getRow(rowIndex);

                // 실제 Excel에서 보이는 행 번호
                int excelRowNumber = rowIndex + 1;

                if (row == null || isEmptyRow(row)) {
                    continue;
                }

                totalCount++;

                try {
                    processExcelRow(row, excelRowNumber);
                    successCount++;

                } catch (IllegalArgumentException e) {
                    errors.add(
                            excelRowNumber + "행: " + e.getMessage()
                    );

                } catch (Exception e) {
                    errors.add(
                            excelRowNumber
                            + "행: 데이터를 처리하는 중 오류가 발생했습니다."
                    );
                }
            }

        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Excel 파일을 읽을 수 없습니다.",
                    e
            );
        }

        int failureCount = errors.size();

        String message;

        if (totalCount == 0) {
            message = "처리할 건강검진 데이터가 없습니다.";

        } else if (failureCount == 0) {
            message = "건강검진 결과 Excel 업로드가 완료되었습니다.";

        } else {
            message = "일부 데이터 처리에 실패했습니다.";
        }

        return new CheckupExcelUploadResponse(
                totalCount,
                successCount,
                failureCount,
                errors,
                message
        );
    }

    /**
     * Excel 한 행의 건강검진 데이터를 처리한다.
     */
    private void processExcelRow(
            Row row,
            int excelRowNumber) {

        // A열: 사번
        String employeeNumber =
                getCellText(row.getCell(0)).trim();

        // B열: 이름
        String employeeName =
                getCellText(row.getCell(1)).trim();

        // C열: 검진연도
        Short checkupYear =
                getShortCellValue(row.getCell(2));

        // D열: 검진일
        LocalDate checkupDate =
                getLocalDateCellValue(row.getCell(3));

        // E열: 검진요약
        String checkupSummary =
                getCellText(row.getCell(4)).trim();

        if (employeeNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "사번이 입력되지 않았습니다."
            );
        }

        if (checkupYear == null) {
            throw new IllegalArgumentException(
                    "검진연도가 입력되지 않았습니다."
            );
        }

        Employee employee =
                employeeDao.findByEmployeeNumberAndStatus(
                        employeeNumber,
                        "Y"
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "재직 중인 직원을 찾을 수 없습니다. "
                                + "(사번: " + employeeNumber + ")"
                        )
                );

        /*
         * 이름은 직원을 찾는 기준으로 사용하지 않는다.
         * 사번으로 찾은 직원의 이름과 Excel 이름이 다르면
         * 잘못된 Excel 데이터로 처리한다.
         */
        if (!employeeName.isBlank()
                && !employee.getName().equals(employeeName)) {

            throw new IllegalArgumentException(
                    "사번과 이름이 일치하지 않습니다. "
                    + "(사번: " + employeeNumber + ")"
            );
        }

        Optional<Checkup> existingCheckup =
                checkupDao
                    .findFirstByEmployeeAndCheckupYearOrderByCheckupIdDesc(
                            employee,
                            checkupYear
                    );

        Checkup checkup;

        if (existingCheckup.isPresent()) {
            // 기존 기록이 있으면 수정한다.
            checkup = existingCheckup.get();

        } else {
            // 기존 기록이 없으면 새 기록을 생성한다.
            checkup = new Checkup();
            checkup.setEmployee(employee);
            checkup.setCheckupYear(checkupYear);
        }

        checkup.setCheckupDate(checkupDate);

        /*
         * 검진요약이 비어 있으면 null로 저장한다.
         */
        checkup.setCheckupSummary(
                checkupSummary.isBlank()
                        ? null
                        : checkupSummary
        );

        checkupDao.save(checkup);
    }

    /**
     * 업로드된 파일이 Excel 파일인지 검증한다.
     */
    private void validateExcelFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "업로드할 Excel 파일을 선택해 주세요."
            );
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null
                || originalFilename.isBlank()) {

            throw new IllegalArgumentException(
                    "파일 이름을 확인할 수 없습니다."
            );
        }

        String lowerFilename =
                originalFilename.toLowerCase(Locale.ROOT);

        if (!lowerFilename.endsWith(".xlsx")
                && !lowerFilename.endsWith(".xls")) {

            throw new IllegalArgumentException(
                    "Excel 파일(.xlsx, .xls)만 업로드할 수 있습니다."
            );
        }
    }

    /**
     * Excel 행 전체가 비어 있는지 확인한다.
     */
    private boolean isEmptyRow(Row row) {

        for (int cellIndex = 0;
                cellIndex < 5;
                cellIndex++) {

            String value =
                    getCellText(row.getCell(cellIndex)).trim();

            if (!value.isBlank()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Excel 셀의 값을 문자열로 변환한다.
     *
     * 숫자, 문자열 등 셀 형식과 관계없이
     * 화면에 표시되는 값으로 읽는다.
     */
    private String getCellText(Cell cell) {

        if (cell == null) {
            return "";
        }

        DataFormatter formatter = new DataFormatter();

        return formatter.formatCellValue(cell);
    }

    /**
     * Excel 셀의 검진연도를 Short 타입으로 변환한다.
     */
    private Short getShortCellValue(Cell cell) {

        if (cell == null
                || cell.getCellType() == CellType.BLANK) {
            return null;
        }

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (short) cell.getNumericCellValue();
            }

            String value = getCellText(cell)
                    .replace("년", "")
                    .trim();

            if (value.isBlank()) {
                return null;
            }

            /*
             * Excel에서 2026이 2026.0으로 읽히는 상황도 처리한다.
             */
            double number = Double.parseDouble(value);

            return (short) number;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "검진연도 형식이 올바르지 않습니다."
            );
        }
    }

    /**
     * Excel 셀의 검진일을 LocalDate 타입으로 변환한다.
     *
     * 지원 형식:
     * 2026-08-01
     * 2026/08/01
     * 2026.08.01
     * Excel 날짜 형식
     */
    private LocalDate getLocalDateCellValue(Cell cell) {

        if (cell == null
                || cell.getCellType() == CellType.BLANK) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC
                && DateUtil.isCellDateFormatted(cell)) {

            return cell.getLocalDateTimeCellValue()
                    .toLocalDate();
        }

        String value = getCellText(cell).trim();

        if (value.isBlank()) {
            return null;
        }

        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ofPattern("yyyy.MM.dd")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(value, formatter);

            } catch (DateTimeParseException e) {
                // 다른 날짜 형식으로 다시 확인한다.
            }
        }

        throw new IllegalArgumentException(
                "검진일 형식이 올바르지 않습니다. "
                + "(예: 2026-08-01)"
        );
    }

    /**
     * 건강검진 수동 알림 발송 이력을 저장한다.
     */
    @Transactional
    public ReminderResponse sendManualReminder(
            ManualReminderRequest request) {

        Checkup checkup =
                checkupDao.findById(request.getCheckupId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "건강검진 기록을 찾을 수 없습니다."
                                )
                        );

        CheckupReminder reminder = new CheckupReminder();

        reminder.setCheckupReminderChannel(
                request.getChannel()
        );
        reminder.setCheckupReminderContent(
                request.getContent()
        );
        reminder.setCheckupReminderSentAt(
                LocalDateTime.now()
        );
        reminder.setCheckupReminderStatus("SUCCESS");
        reminder.setCheckupReminderIsManual(true);
        reminder.setCheckup(checkup);

        CheckupReminder savedReminder =
                checkupReminderDao.save(reminder);

        return new ReminderResponse(
                savedReminder.getCheckupReminderId(),
                savedReminder.getCheckup().getCheckupId(),
                savedReminder.getCheckupReminderChannel(),
                savedReminder.getCheckupReminderContent(),
                savedReminder.getCheckupReminderSentAt(),
                savedReminder.getCheckupReminderStatus(),
                savedReminder.isCheckupReminderIsManual()
        );
    }

    /**
     * 자동 알림 설정을 등록한다.
     */
    @Transactional
    public ReminderSettingResponse createReminderSetting(
            ReminderSettingRequest request) {

        CheckupReminderSetting setting =
                new CheckupReminderSetting();

        setting.setCheckupReminderSettingType(
                request.getSettingType()
        );
        setting.setCheckupReminderSettingMessageTemplate(
                request.getMessageTemplate()
        );
        setting.setCheckupReminderSettingCronSchedule(
                request.getCronSchedule()
        );
        setting.setCheckupReminderSettingIsActive(
                request.isActive()
        );

        CheckupReminderSetting savedSetting =
                checkupReminderSettingDao.save(setting);

        return convertReminderSettingResponse(savedSetting);
    }

    /**
     * 자동 알림 설정 전체 목록을 조회한다.
     */
    @Transactional(readOnly = true)
    public List<ReminderSettingResponse> getReminderSettings() {

        return checkupReminderSettingDao.findAll()
                .stream()
                .map(this::convertReminderSettingResponse)
                .toList();
    }

    /**
     * 자동 알림 설정을 수정한다.
     */
    @Transactional
    public ReminderSettingResponse updateReminderSetting(
            Long settingId,
            ReminderSettingRequest request) {

        CheckupReminderSetting setting =
                checkupReminderSettingDao.findById(settingId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "자동 알림 설정을 찾을 수 없습니다."
                                )
                        );

        setting.setCheckupReminderSettingType(
                request.getSettingType()
        );
        setting.setCheckupReminderSettingMessageTemplate(
                request.getMessageTemplate()
        );
        setting.setCheckupReminderSettingCronSchedule(
                request.getCronSchedule()
        );
        setting.setCheckupReminderSettingIsActive(
                request.isActive()
        );

        CheckupReminderSetting savedSetting =
                checkupReminderSettingDao.save(setting);

        return convertReminderSettingResponse(savedSetting);
    }

    /**
     * 자동 알림 설정 Entity를 응답 DTO로 변환한다.
     */
    private ReminderSettingResponse convertReminderSettingResponse(
            CheckupReminderSetting setting) {

        return new ReminderSettingResponse(
                setting.getCheckupReminderSettingId(),
                setting.getCheckupReminderSettingType(),
                setting.getCheckupReminderSettingMessageTemplate(),
                setting.getCheckupReminderSettingCronSchedule(),
                setting.isCheckupReminderSettingIsActive()
        );
    }

    /**
     * 건강검진 알림 발송 이력 전체를 최신순으로 조회한다.
     */
    @Transactional(readOnly = true)
    public List<ReminderResponse> getReminderHistory() {

        return checkupReminderDao
                .findAllByOrderByCheckupReminderSentAtDesc()
                .stream()
                .map(reminder -> new ReminderResponse(
                        reminder.getCheckupReminderId(),
                        reminder.getCheckup().getCheckupId(),
                        reminder.getCheckupReminderChannel(),
                        reminder.getCheckupReminderContent(),
                        reminder.getCheckupReminderSentAt(),
                        reminder.getCheckupReminderStatus(),
                        reminder.isCheckupReminderIsManual()
                ))
                .toList();
    }
}