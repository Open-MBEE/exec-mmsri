//package org.openmbee.mms.mmsri.config;
//
//import java.io.IOException;
//import java.util.UUID;
//import jakarta.servlet.Filter;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//public class LoggingFilter implements Filter {
//    private final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);
//
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
//            throws IOException, ServletException {
//        String corr = UUID.randomUUID().toString();
//        long time = System.currentTimeMillis();
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String user = "anonymousUser";
//        if (auth != null) {
//            user = auth.getName();
//        }
//        HttpServletRequest r = (HttpServletRequest) req;
//        String query = r.getQueryString();
//        query = query == null ? "" : ("?" + query);
//        if (!r.getRequestURI().startsWith("/actuator") && !r.getRequestURI().startsWith("/v3")) {
//            LOGGER.info("req - {} - {} - {} - {}", user, r.getMethod(), r.getRequestURI() + query,
//                    corr);
//        }
//        chain.doFilter(req, resp);
//
//        time = System.currentTimeMillis() - time;
//        HttpServletResponse res = (HttpServletResponse) resp;
//        auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null) {
//            user = auth.getName();
//        }
//        if (!r.getRequestURI().startsWith("/actuator") && !r.getRequestURI().startsWith("/v3")) {
//            LOGGER.info("res - {} - {} - {} - {} - {} - {}ms ", user, r.getMethod(),
//                    r.getRequestURI() + query, corr, res.getStatus(), time);
//        }
//    }
//}

package org.openmbee.mms.mmsri.config;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class LoggingFilter implements Filter {
    private final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        String corr = UUID.randomUUID().toString();
        long time = System.currentTimeMillis();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = "anonymousUser";
        if (auth != null) {
            user = auth.getName();
        }
        HttpServletRequest r = (HttpServletRequest) req;
        String query = r.getQueryString();
        query = query == null ? "" : ("?" + query);
        if (!r.getRequestURI().startsWith("/actuator") && !r.getRequestURI().startsWith("/v3")) {
            LOGGER.info("req - {} - {} - {} - {}", user, r.getMethod(), r.getRequestURI() + query,
                    corr);
        }
        chain.doFilter(req, resp);

        time = System.currentTimeMillis() - time;
        HttpServletResponse res = (HttpServletResponse) resp;
        auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            user = auth.getName();
        }
        if (!r.getRequestURI().startsWith("/actuator") && !r.getRequestURI().startsWith("/v3")) {
            LOGGER.info("res - {} - {} - {} - {} - {} - {}ms ", user, r.getMethod(),
                    r.getRequestURI() + query, corr, res.getStatus(), time);
        }
    }
}
