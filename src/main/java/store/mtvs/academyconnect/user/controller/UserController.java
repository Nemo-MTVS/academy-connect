package store.mtvs.academyconnect.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import store.mtvs.academyconnect.user.service.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/teacher/login")
    public String teacherLogin() {
        return "teacher/login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "user/signup";
    }

    @PostMapping("/signup")
    public String registerUser(@RequestParam String loginId,
                              @RequestParam String password,
                              @RequestParam String passwordConfirm,
                              @RequestParam String name,
                              @RequestParam String className,
                              RedirectAttributes redirectAttributes) {
        
        // 비밀번호 확인 검증
        if (!password.equals(passwordConfirm)) {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/signup";
        }

        try {
            userService.registerUser(loginId, className, password, name);
            return "redirect:/login?success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/signup";
        }
    }
}

