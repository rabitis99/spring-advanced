package org.example.expert.config;



import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class AdminApiInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AdminApiInterceptor.class);

    private final JwtUtil jwtUtil;  // JWT 유틸리티 클래스 (필드 주입 또는 생성자 주입 가능)

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String requestURI = request.getRequestURI();

        // 어드민 API만 감지
        if (!requestURI.startsWith("/admin")) {
            return true; // 어드민 API가 아니면 통과
        }

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "JWT 토큰이 필요합니다.");
            return false;
        }

        try {
            String jwt = jwtUtil.substringToken(bearerToken);
            Claims claims = jwtUtil.extractClaims(jwt);

            if (claims == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "JWT 토큰이 유효하지 않습니다.");
                return false;
            }

            String role = claims.get("userRole", String.class);
            Long userId = Long.parseLong(claims.getSubject());

            if (!"ADMIN".equals(role)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "어드민 권한이 없습니다.");
                return false;
            }

            // ✅ 사전 인증 통과 후, 로깅
            logger.info("[어드민 접근] 사용자 ID: {}, 시간: {}, URL: {}",
                    userId, LocalDateTime.now(), requestURI);

            return true;
        } catch (Exception e) {
            logger.error("JWT 검증 중 오류 발생", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 인증 실패");
            return false;
        }
    }
}

