package com.kh.healthgate.config;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kh.healthgate.auth.controller.AuthController;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie; // 💥 쿠키 임포트 추가
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. CORS Preflight 요청은 인터셉터 통과
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 💥 HTTP 헤더 대신 "브라우저 쿠키"에서 accessToken 추출
        String token = null;
        Cookie[] cookies = request.getCookies(); // 브라우저가 들고 온 쿠키 배열 꺼내기
        
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue(); // 토큰 값 쏙 빼오기
                    break;
                }
            }
        }

        // 3. 토큰을 성공적으로 찾은 경우 검증 진행
        if (StringUtils.hasText(token)) {
            try {
                // AuthController에 정의된 SECRET_KEY로 복호화 키 생성
                Key key = Keys.hmacShaKeyFor(AuthController.SECRET_KEY.getBytes(StandardCharsets.UTF_8));

                // 토큰 검증 및 파싱. 만료 혹은 위조 시 여기서 예외 발생
                Claims claims = Jwts.parserBuilder()
                                    .setSigningKey(key)
                                    .build()
                                    .parseClaimsJws(token)
                                    .getBody();

                // 4. 로그인한 사원 정보는 여기서 꺼내서 쓸 수 있음. (기존 로직 유지)
                request.setAttribute("employeeNumber", claims.getSubject());
                request.setAttribute("empRole", claims.get("role"));

                Object idValue = claims.get("id");
                if (idValue instanceof Number) {
                    request.setAttribute("empId", ((Number) idValue).longValue());
                } else {
                    request.setAttribute("empId", idValue);
                }

                return true;
                
            } catch(Exception e) {
                sendUnauthorizedResponse(response, "유효하지 않거나 만료된 토큰입니다.");
                return false;
            }
        }
        
        // 쿠키에 토큰이 아예 없는 경우
        sendUnauthorizedResponse(response, "인증 토큰이 누락되었습니다.");
        return false;
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
}
