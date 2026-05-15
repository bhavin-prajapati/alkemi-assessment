package com.developer.test.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        Exception exception = null;

        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            exception = ex;
            throw ex;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            if (exception != null && status < 400) {
                status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            }

            if (exception != null) {
                logger.error("request.method={} request.path={} response.status={} response.duration_ms={} error.message={}",
                        request.getMethod(),
                        request.getRequestURI(),
                        status,
                        duration,
                        exception.getMessage(),
                        exception);
            } else {
                logger.info("request.method={} request.path={} response.status={} response.duration_ms={}",
                        request.getMethod(),
                        request.getRequestURI(),
                        status,
                        duration);
            }
        }
    }
}
