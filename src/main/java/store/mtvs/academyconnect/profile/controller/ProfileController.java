package store.mtvs.academyconnect.profile.controller;

import lombok.extern.slf4j.Slf4j;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.mtvs.academyconnect.global.config.CustomUserDetails;
import store.mtvs.academyconnect.profile.domain.entity.Profile;
import store.mtvs.academyconnect.profile.dto.ProfileDTO;
import store.mtvs.academyconnect.profile.service.ProfileService;
import store.mtvs.academyconnect.user.domain.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import store.mtvs.academyconnect.user.domain.entity.User;
import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/student/profile")
public class ProfileController {
    public String renderMarkdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // 학생 리스트 페이지
    @GetMapping
    public String showProfilePage(@RequestParam(value = "className", required = false) String className, Model model) {
        try {
            String selectedClass = className != null ? className : "Backend";
            log.info("Selected class: {}", selectedClass);

            model.addAttribute("classGroups", List.of("Backend", "Unity", "TA"));

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

    // 학생 상세 페이지
    @GetMapping("/{userId}")
    public String showUserProfile(@PathVariable String userId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            boolean isMyProfile = false;
            log.info("Showing profile for user: {}", userId);

            User user = profileService.getUserById(userId);
            Profile profile = profileService.getProfileByUserId(userId);

            model.addAttribute("user", user);
            model.addAttribute("profile", profile);
            if (userId.equals(userDetails.getId())) {
                isMyProfile = true;
            }
            model.addAttribute("IsMyProfile", isMyProfile);
            String markdown = profile.getMd(); // 사용자가 저장한 마크다운
            String html = MarkdownUtil.convertToHtml(markdown);
            profile.setMd(html); // Thymeleaf에서 th:utext로 출력할 수 있도록 HTML로 변환된 내용을 넣기

            return "profile/detail";
        } catch (Exception e) {
            log.error("Error in showUserProfile: ", e);
            throw e;
        }
    }

    // 학생 프로필 수정 폼
    @GetMapping("/{userId}/edit")
    public String editProfile(@PathVariable String userId, Model model, @AuthenticationPrincipal CustomUserDetails customUserDetails) throws  AccessDeniedException {
        try {
            String userUuid = customUserDetails.getId();
            if (!userUuid.equals(userId)) {
                throw new AccessDeniedException("권한이 없습니다");
            }
            User user = profileService.getUserById(userId);
            Profile profile = profileService.getProfileByUserId(userId);

            model.addAttribute("student", user);
            model.addAttribute("profile", profile);


            return "profile/profile-edit";
        } catch (Exception e) {
            log.error("Error in editProfile: ", e);
            throw e;
        }
    }

    // 학생 프로필 수정 저장
    @PostMapping("/{userId}/update")
    public String updateProfile(@PathVariable String userId,
                                @ModelAttribute ProfileDTO form,
                                @AuthenticationPrincipal CustomUserDetails customUserDetails
                                ) throws IOException {
        String userUuid = customUserDetails.getId();
        if (!userUuid.equals(userId)) {
            throw new AccessDeniedException("권한이 없습니다");
        }

        String email = form.getEmail();
        String github = form.getGithub();
        String blog = form.getBlog();
        String bio = form.getBio();
        MultipartFile profileImage = form.getProfileImage();

        Profile existing = profileService.getProfileByUserId(userId);
        String profileImagePath = existing.getFilePath();

        if (profileImage != null && !profileImage.isEmpty()) {
            String uploadDir = "C:/upload/profile/";
            String fileName = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();
            File saveFile = new File(uploadDir, fileName);
            saveFile.getParentFile().mkdirs();
            profileImage.transferTo(saveFile);
            profileImagePath = "/student/profile-images/" + fileName;
        }

        profileService.updateProfile(userId, email, github, blog, bio, profileImagePath);


        return "redirect:/student/profile/" + userId;
    }
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/403"; // 에러 페이지로 이동
    }
}
