package org.example.expert.config;

import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
public class AdminApiLoggingAspect {

    private final JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(AdminApiLoggingAspect.class);

    @Around("execution(* org.example.expert.domain.comment.controller.CommentAdminController.deleteComment(..)) || " +
            "execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))")

    public Object logAdminApi(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String bearerToken=request.getHeader("Authorization");
        String jwt = jwtUtil.substringToken(bearerToken);
        Claims claims = jwtUtil.extractClaims(jwt);
        Long userId = Long.parseLong(claims.getSubject());
        String url = request.getRequestURI();
        LocalDateTime timestamp = LocalDateTime.now();

        Object[] args = joinPoint.getArgs();
        String requestBody = Arrays.stream(args)
                .map(arg -> new Gson().toJson(arg))
                .collect(Collectors.joining(", "));

        logger.info("[어드민 API 요청] 사용자ID: {}, 시간: {}, URL: {}, 요청본문: {}",
                userId, timestamp, url, requestBody);

        Object response = joinPoint.proceed();

        String responseBody = new Gson().toJson(response);
        logger.info("[어드민 API 응답] 사용자ID: {}, 응답본문: {}", userId, responseBody);

        return response;
    }
}
