package com.kh.healthgate.attendance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.healthgate.attendance.model.service.AttendanceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin
@RestController
@Tag(name = "출근자 정보 API", description = "출근자 건강상태 관련 API")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    public record AttendanceDto(
        @Schema(example = "70", description = "당일 정상 출근 횟수") Long attendanceCount,
        @Schema(example = "5", description = "당일 주의 출근 횟수") Long warnCount,
        @Schema(example = "3", description = "당일 출근 거부 횟수") Long denyCount
    ) {}


    @GetMapping("/attendances/count")
    @Operation(summary = "출근자 상태 카운트 조회", description = "당일 출근/주의/근무불가 상태 조회")
    public ResponseEntity<AttendanceDto> getAttendanceCount() {

        AttendanceDto ad = attendanceService.getAttendanceCount();

        return ResponseEntity.ok(ad);
    }

}
