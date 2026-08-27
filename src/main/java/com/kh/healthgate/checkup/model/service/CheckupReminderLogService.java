package com.kh.healthgate.checkup.model.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.healthgate.checkup.model.dao.CheckupReminderDao;
import com.kh.healthgate.checkup.model.vo.CheckupReminder;

@Service
public class CheckupReminderLogService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CheckupReminderDao checkupReminderDao;

    public CheckupReminderLogService(
            CheckupReminderDao checkupReminderDao) {

        this.checkupReminderDao = checkupReminderDao;
    }

    /**
     * 건강검진 알림 발송 이력을 Excel 파일로 생성한다.
     *
     * @param channel 발송 채널(SMS, EMAIL), null이면 전체
     * @param manual 수동 여부, null이면 전체
     * @return Excel 파일 데이터
     */
    @Transactional(readOnly = true)
    public byte[] downloadReminderHistoryExcel(
            String channel,
            Boolean manual) {

        List<CheckupReminder> reminderList =
                checkupReminderDao
                        .findAllByOrderByCheckupReminderSentAtDesc()
                        .stream()
                        .filter(reminder ->
                                isChannelMatched(reminder, channel)
                        )
                        .filter(reminder ->
                                manual == null
                                || reminder.isCheckupReminderIsManual()
                                        == manual
                        )
                        .toList();

        try (
            Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream()
        ) {
            Sheet sheet =
                    workbook.createSheet("알림 발송 이력");

            CellStyle headerStyle =
                    createHeaderStyle(workbook);

            createHeaderRow(sheet, headerStyle);

            int rowIndex = 1;

            for (CheckupReminder reminder : reminderList) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(
                        reminder.getCheckupReminderId()
                );

                row.createCell(1).setCellValue(
                        reminder.getCheckup().getCheckupId()
                );

                row.createCell(2).setCellValue(
                        reminder.getCheckupReminderChannel() == null
                                ? ""
                                : reminder
                                    .getCheckupReminderChannel()
                                    .name()
                );

                row.createCell(3).setCellValue(
                        reminder.getCheckupReminderContent() == null
                                ? ""
                                : reminder
                                    .getCheckupReminderContent()
                );

                row.createCell(4).setCellValue(
                        reminder.getCheckupReminderSentAt() == null
                                ? ""
                                : reminder
                                    .getCheckupReminderSentAt()
                                    .format(DATE_TIME_FORMATTER)
                );

                row.createCell(5).setCellValue(
                        convertStatus(
                                reminder
                                    .getCheckupReminderStatus()
                        )
                );

                row.createCell(6).setCellValue(
                        reminder.isCheckupReminderIsManual()
                                ? "수동"
                                : "자동"
                );
            }

            setColumnWidths(sheet);

            workbook.write(outputStream);

            return outputStream.toByteArray();

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "알림 발송 이력 Excel 파일 생성에 실패했습니다.",
                    exception
            );
        }
    }

    private boolean isChannelMatched(
            CheckupReminder reminder,
            String channel) {

        if (
            channel == null
            || channel.isBlank()
            || "ALL".equalsIgnoreCase(channel)
        ) {
            return true;
        }

        if (reminder.getCheckupReminderChannel() == null) {
            return false;
        }

        return reminder
                .getCheckupReminderChannel()
                .name()
                .equalsIgnoreCase(channel);
    }

    private CellStyle createHeaderStyle(
            Workbook workbook) {

        Font font = workbook.createFont();
        font.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);

        return style;
    }

    private void createHeaderRow(
            Sheet sheet,
            CellStyle headerStyle) {

        String[] headers = {
            "번호",
            "검진 ID",
            "발송 채널",
            "메시지 내용",
            "발송 일시",
            "상태",
            "발송 구분"
        };

        Row headerRow = sheet.createRow(0);

        for (int index = 0; index < headers.length; index++) {
            headerRow
                    .createCell(index)
                    .setCellValue(headers[index]);

            headerRow
                    .getCell(index)
                    .setCellStyle(headerStyle);
        }
    }

    private void setColumnWidths(Sheet sheet) {

        sheet.setColumnWidth(0, 12 * 256);
        sheet.setColumnWidth(1, 12 * 256);
        sheet.setColumnWidth(2, 14 * 256);
        sheet.setColumnWidth(3, 60 * 256);
        sheet.setColumnWidth(4, 22 * 256);
        sheet.setColumnWidth(5, 12 * 256);
        sheet.setColumnWidth(6, 12 * 256);
    }

    private String convertStatus(String status) {

        if ("SUCCESS".equalsIgnoreCase(status)) {
            return "성공";
        }

        if ("FAILED".equalsIgnoreCase(status)) {
            return "실패";
        }

        return status == null ? "" : status;
    }
}