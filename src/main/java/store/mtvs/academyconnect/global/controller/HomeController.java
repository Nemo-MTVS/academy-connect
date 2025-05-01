package store.mtvs.academyconnect.global.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import store.mtvs.academyconnect.user.domain.enums.UserRole;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // TEACHER 권한 확인
        if (authentication != null && authentication.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_" + UserRole.TEACHER.name()))) {
            // TEACHER 권한이면 스케줄링 페이지로 리다이렉트
            return "redirect:/teacher/scheduling";
        }

        // 그 외에는 메인 페이지 표시
        return "index";
    }
}