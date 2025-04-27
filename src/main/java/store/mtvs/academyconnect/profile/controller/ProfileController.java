package store.mtvs.academyconnect.profile.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.mtvs.academyconnect.profile.domain.entity.Profile;
import store.mtvs.academyconnect.profile.service.ProfileService;
import store.mtvs.academyconnect.user.domain.entity.User;

import java.util.List;

@Slf4j
@Controller// 스프링 MVC에서 이 클래스를 컨트롤러로 인식하도록 지정
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;// UserService 타입의 서비스를 주입받기 위한 필드

    // 생성자를 통한 의존성 주입 (Dependency Injection):
    // 스프링 컨테이너가 UserService 인스턴스를 자동으로 주입(할당)해줍니다.
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }
    /**
     * GET 요청을 처리하는 메서드.
     * "/profile/{id}" URL로 GET 요청이 들어오면 이 메서드가 실행됩니다.
     * @PathVariable은 URL 경로의 {id} 부분을 메서드의 파라미터로 받아옵니다.
     * Model 객체는 컨트롤러에서 뷰로 데이터를 전달할 때 사용합니다.
     */
    @GetMapping
    public String showProfilePage(@RequestParam(value = "className", required = false) String className, Model model) {
        try {
            // 기본적으로 백엔드 반의 학생들을 보여줌
            String selectedClass = className != null ? className : "Backend";
            log.info("Selected class: {}", selectedClass);
            
            model.addAttribute("classGroups", List.of("Backend", "Unity", "TA"));

            // 선택된 반의 학생들 조회
            List<User> students = profileService.getStudentsByClassName(selectedClass);
            log.info("Found {} students in class {}", students.size(), selectedClass);
            
            model.addAttribute("students", students);
            model.addAttribute("selectedClass", selectedClass);
            
            return "profile/profile";
        } catch (Exception e) {
            log.error("Error in showProfilePage: ", e);
            throw e;
        }
    }

    @GetMapping("/{userId}")
    public String showUserProfile(@PathVariable String userId, Model model) {
        try {
            log.info("Showing profile for user: {}", userId);
            User user = profileService.getUserById(userId);
            Profile profile = profileService.getProfileByUserId(userId);
            
            model.addAttribute("user", user);
            model.addAttribute("profile", profile);
            
            return "profile/detail";
        } catch (Exception e) {
            log.error("Error in showUserProfile: ", e);
            throw e;
        }
    }
}