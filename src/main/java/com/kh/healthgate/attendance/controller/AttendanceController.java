package com.kh.healthgate.attendance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.healthgate.attendance.model.service.AttendanceService;

@CrossOrigin
@RestController
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    public record AttendanceDto(
        Long attendanceCount,
        Long warnCount,
        Long denyCount
    ) {}


    @GetMapping("/dattendances")
    public ResponseEntity<AttendanceDto> getAttendanceCount() {

        AttendanceDto ad = attendanceService.getAttendanceCount();

        return ResponseEntity.ok(ad);
    }

}
