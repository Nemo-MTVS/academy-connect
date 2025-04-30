package store.mtvs.academyconnect.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.stream.Collectors;

@Slf4j
public class RequestDebugFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        long startTime = System.currentTimeMillis();
        
        // 요청 정보 로깅
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String queryString = request.getQueryString() != null ? request.getQueryString() : "";
        String remoteAddr = request.getRemoteAddr();
        
        // 모든 요청 헤더 수집
        StringBuilder headers = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.append(headerName).append(": ").append(request.getHeader(headerName)).append(", ");
        }
        
        // 현재 인증 정보 로깅
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "anonymousUser";
        String authorities = authentication != null ? 
                authentication.getAuthorities().stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", ")) : "none";
        
        // 디버그 정보 출력
        log.info("==================== REQUEST DEBUG INFO ====================");
        log.info("Request: {} {} {}", method, requestURI, queryString);
        log.info("Remote IP: {}", remoteAddr);
        log.info("Headers: {}", headers);
        log.info("User: {}", username);
        log.info("Authorities: {}", authorities);
        log.info("===========================================================");
        
        try {
            filterChain.doFilter(request, response);
        } finally {
            // 응답 정보 로깅
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            
            log.info("==================== RESPONSE DEBUG INFO ===================");
            log.info("Response Status: {}", status);
            log.info("Processing Time: {} ms", duration);
            log.info("===========================================================");
        }
    }
} 