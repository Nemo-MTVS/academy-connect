package store.mtvs.academyconnect.global.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import store.mtvs.academyconnect.user.domain.enums.UserRole;

import java.io.IOException;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, 
                               Authentication authentication) throws IOException, ServletException {
        
        // 로그아웃 시 사용자가 이미 인증되지 않은 상태가 될 수 있으므로 null 체크
        if (authentication != null) {
            boolean isTeacher = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(authority -> authority.equals("ROLE_" + UserRole.TEACHER.name()));
            
            if (isTeacher) {
                // 교사는 교사 로그인 페이지로 리다이렉트
                response.sendRedirect("/teacher/login?logout");
                return;
            }
        }
        
        // 그 외 사용자는 일반 로그인 페이지로 리다이렉트
        response.sendRedirect("/login?logout");
    }
} 