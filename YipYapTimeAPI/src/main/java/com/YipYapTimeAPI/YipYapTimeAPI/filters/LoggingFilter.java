package com.YipYapTimeAPI.YipYapTimeAPI.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Slf4j
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

        // Log request start
        logRequestStart(httpRequest);

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            // Log request completion
            logRequestCompletion();
        }
    }

    private void logRequestStart(HttpServletRequest request) {
        Instant startTime = Instant.now();
        MDC.put("startTime", startTime.toString());

        String requestId = request.getHeader("X-Request-ID");
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }
        MDC.put("requestId", requestId);
        MDC.put("httpMethod", request.getMethod());
        MDC.put("httpRequestURI", request.getRequestURI());

        log.info("Start {} uri={}", request.getMethod(), request.getRequestURI());
    }

    private void logRequestCompletion() {
        String startTimeStr = MDC.get("startTime");
        if (startTimeStr != null) {
            Instant startTime = Instant.parse(startTimeStr); // Parse stored string to Instant
            long duration = Instant.now().toEpochMilli() - startTime.toEpochMilli();

            log.info("Took duration={}ms {} uri={}", duration, MDC.get("httpMethod"), MDC.get("httpRequestURI"));
        } else {
            log.warn("Unable to calculate duration for requestId={} method={} uri={}. Start time not found in MDC.", MDC.get("requestId"), MDC.get("httpMethod"), MDC.get("httpRequestURI"));
        }

        MDC.clear();

    }
}
