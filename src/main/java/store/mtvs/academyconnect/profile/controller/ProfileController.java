package store.mtvs.academyconnect.profile.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.mtvs.academyconnect.profile.domain.entity.Profile;
import store.mtvs.academyconnect.profile.service.ProfileService;
import store.mtvs.academyconnect.user.domain.entity.User;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

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
     *
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

    // 프로필 수정 폼
    @GetMapping("/{userId}/edit")
    public String editProfile(@PathVariable String userId, Model model) {
        User user = profileService.getUserById(userId);
        Profile profile = profileService.getProfileByUserId(userId);
        // 임시 데이터 (수정 폼에 기본 값 채우기)
        model.addAttribute("student", user);
        model.addAttribute("profile", profile);
//        model.addAttribute("name", "홍길동");
//        model.addAttribute("email", "hong@example.com");
//        model.addAttribute("bio", "안녕하세요!");
        return "profile/profile-edit"; // profile-edit.html
    }

    // 프로필 수정 저장
    @PostMapping("/{userId}/update")
    public String updateProfile(@PathVariable String userId,
                                @RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String github,
                                @RequestParam String blog,
                                @RequestParam(required = false) MultipartFile profileImage,
                                Model model) throws IOException {

        String profileImagePath = null;

        // 파일 저장 처리
        if (profileImage != null && !profileImage.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();
            File saveFile = new File("src/main/resources/static/images/" + fileName);
            profileImage.transferTo(saveFile);
            profileImagePath = "/images/" + fileName;
        }

        // 서비스에 업데이트 요청
        profileService.updateProfile(userId, name, email, github, blog);
        return "redirect:/profile/" + userId;
    }
//    @PostMapping("/update")
//    public String updateProfile(@RequestParam String userId,
//                                @RequestParam String name,
//                                @RequestParam String email,
//                                @RequestParam String bio,
//                                @RequestParam(required = false) MultipartFile profileImage,
//                                Model model) throws IOException {
//
//        // 프로필 이미지 저장 경로 설정 (기본 경로 예시)
//        String profileImagePath = "/images/default.png";  // 기본 이미지 경로
//
//        // 파일이 있을 경우 저장 처리
//        if (profileImage != null && !profileImage.isEmpty()) {
//            // 파일 이름을 UUID로 생성하고, 파일을 저장할 경로 설정
//            String fileName = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();
//            File saveFile = new File("src/main/resources/static/images/" + fileName);
//            // 이미지 파일을 지정된 경로에 저장
//            profileImage.transferTo(saveFile);
//
//            // 저장된 파일 경로를 DB에 저장할 수 있게 변수 업데이트
//            profileImagePath = "/images/" + fileName;
//
//        }
//        // 1. 프로필 업데이트 로직
//        // DB에서 사용자 프로필을 찾아서 업데이트
//        // 예시로 서비스에서 업데이트하는 코드
//        Profile profile = profileService.getProfileByUserId(userId); // userId를 받아서 프로필 조회
//        User user = profileService.getUserById(userId);
//        user.changeName(name);
//        profile.setEmail(email);
//
//
//        profileService.updateProfile(profile); // DB에 업데이트하는 서비스 호출
//        model.addAttribute("name", name);
//        model.addAttribute("email", email);
//        model.addAttribute("bio", bio);
//        return "profile"; // 수정 후 다시 profile.html 보여줌
//    }
}