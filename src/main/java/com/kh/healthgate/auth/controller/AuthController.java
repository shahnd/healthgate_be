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
import com.kh.healthgate.employee.model.vo.role;

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

    public record LoginResponse(String accessToken, String refreshToken, String employeeNumber, String name, role role, Long id) {}

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> loginEmployee(@RequestBody Employee employee) {

        log.debug("로그인 정보: {}", employee);

        Employee loginEmp = authService.loginEmployee(employee.getEmployeeNumber());

        if (loginEmp != null && bCryptPasswordEncoder.matches(employee.getPassword(), loginEmp.getPassword())) {
            Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

            String jwt = Jwts.builder()
                             .setSubject(loginEmp.getEmployeeNumber())
                             .claim("id", loginEmp.getId())
                             .claim("name", loginEmp.getName())
                             .claim("role", loginEmp.getRole())
                             .setIssuedAt(new Date(System.currentTimeMillis() + 1 * 60 * 60 * 1000))
                             .signWith(key, SignatureAlgorithm.HS256)
                             .compact();

            LoginResponse responseBody = new LoginResponse(jwt, null, loginEmp.getEmployeeNumber(), loginEmp.getName(), loginEmp.getRole(), loginEmp.getId());

            return ResponseEntity.ok(responseBody);
        }


        return ResponseEntity.ok(null);
    }
}
