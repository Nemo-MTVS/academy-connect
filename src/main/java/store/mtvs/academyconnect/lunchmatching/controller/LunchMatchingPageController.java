package store.mtvs.academyconnect.lunchmatching.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import store.mtvs.academyconnect.user.service.UserService;

@Controller
@RequiredArgsConstructor
public class LunchMatchingPageController {

    private final UserService userService;

    /**
     * 점심 매칭 화면 진입 API
     * [GET] /student/lunchmatching
     */
    @GetMapping("/student/lunchmatching")
    public String lunchmatchingPage(Model model) {
        String userId = getCurrentUserId(); // 로그인한 유저 UUID 가져옴
        String userRole = getCurrentUserRole();  // 로그인한 유저의 역할 (STUDENT, INSTRUCTOR 등)
        model.addAttribute("currentUserId", userId);
        model.addAttribute("currentUserRole", userRole);
        return "lunchmatching/lunchmatching";
    }

    /**
     * 현재 로그인한 사용자 UUID 가져오기
     */
    private String getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userService.getUserByLoginId(userDetails.getUsername())
                    .map(user -> user.getId())
                    .orElse(null);
        }
        return null;
    }

    private String getCurrentUserRole() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userService.getUserByLoginId(userDetails.getUsername())
                    .map(user -> user.getRole()) // "STUDENT", "INSTRUCTOR" 등
                    .orElse(null);
        }
        return null;
    }
}