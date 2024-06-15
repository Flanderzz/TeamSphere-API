package com.YipYapTimeAPI.YipYapTimeAPI.Interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, @Nullable  HttpServletResponse response, @Nullable Object handler) {
        Instant startTime = Instant.now();

        String requestId = request.getHeader("X-Request-ID");
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }

        MDC.put("startTime", startTime.toString());
        MDC.put("httpMethod", request.getMethod());
        MDC.put("httpRequestURI", request.getRequestURI());
        MDC.put("requestId", requestId);

        log.info("START requestId={} method={} uri={}", requestId, request.getMethod(), request.getRequestURI());

        return true;
    }

    @Override
    public void afterCompletion( @Nullable HttpServletRequest request,  @Nullable HttpServletResponse response,   @Nullable Object handler, Exception ex) {
        Instant startTime = Instant.parse(MDC.get("startTime"));
        long duration = Instant.now().toEpochMilli() - startTime.toEpochMilli();

        log.info("TOOK requestId={} duration={}ms method={} uri={}", MDC.get("requestId"), duration, MDC.get("httpMethod"), MDC.get("httpRequestURI"));

        MDC.clear();
    }
}
