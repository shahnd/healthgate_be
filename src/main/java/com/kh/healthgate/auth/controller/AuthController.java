package com.kh.healthgate.auth.controller;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kh.healthgate.auth.model.service.AuthService;
import com.kh.healthgate.employee.model.vo.Employee;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
public class AuthController {

    public static final String SECRET_KEY = "Hello123ThisisHellPangWeWantToBreakTime";

    @Autowired
    private AuthService authService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/auth/login")
    public ResponseEntity<String> loginEmployee(@RequestBody Employee employee) {

        log.debug("로그인 정보: {}", employee);

        Employee loginEmp = authService.loginEmployee(employee.getEmployeeNo());

        if (loginEmp != null && bCryptPasswordEncoder.matches(loginEmp.getEmployeePwd(), employee.getEmployeePwd())) {
            Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

            String jwt = Jwts.builder()
                             .setSubject(loginEmp.getEmployeeNo())
                             .claim("employeeName", loginEmp.getEmployeeName())
                             .claim("employeeRole", loginEmp.getEmployeeRole())
                             .setIssuedAt(new Date(System.currentTimeMillis() + 1 * 60 * 60 * 1000))
                             .signWith(key, SignatureAlgorithm.HS256)
                             .compact();

            return ResponseEntity.ok(jwt);
        }


        return ResponseEntity.ok(null);
    }
}
