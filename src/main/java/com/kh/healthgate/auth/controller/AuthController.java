package com.kh.healthgate.auth.controller;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@Tag(name = "인증 API", description = "로그인")
public class AuthController {

    public static final String SECRET_KEY = "Hello123ThisisHellPangWeWantToBreakTime";

    @Autowired
    private AuthService authService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public record LoginResponse(String accessToken, String refreshToken, String employeeNumber, String name, role role, Long id) {}

    @PostMapping("/auth/login")
    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인합니다.")
    public ResponseEntity<LoginResponse> loginEmployee(@RequestBody Employee employee) {

        Employee loginEmp = authService.loginEmployee(employee.getEmployeeNumber());

        if (loginEmp != null && bCryptPasswordEncoder.matches(employee.getPassword(), loginEmp.getPassword())) {
            Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

            String jwt = Jwts.builder()
                             .setSubject(loginEmp.getEmployeeNumber())
                             .claim("id", loginEmp.getId())
                             .claim("name", loginEmp.getName())
                             .claim("role", loginEmp.getRole())
                             .claim("id", loginEmp.getId())
                             .setIssuedAt(new Date(System.currentTimeMillis() + 1 * 60 * 60 * 1000))
                             .signWith(key, SignatureAlgorithm.HS256)
                             .compact();

            ResponseCookie jwtCookie = ResponseCookie.from("accessToken", jwt)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(60*60)
                    .sameSite("Lax")
                    .build();

            LoginResponse responseBody = new LoginResponse(null, null, loginEmp.getEmployeeNumber(), loginEmp.getName(), loginEmp.getRole(), loginEmp.getId());

            return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                        .body(responseBody);
        }


        return ResponseEntity.ok(null);
    }

    @PostMapping("/auth/logout")
    @Operation(summary = "로그아웃", description = "쿠키를 만료시켜 브라우저에서 로그아웃 시킵니다.")
    public ResponseEntity<?> logoutEmployee() {
        
        ResponseCookie deleteCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();


        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteCookie.toString()).body("로그아웃 성공");
    }
    
}
