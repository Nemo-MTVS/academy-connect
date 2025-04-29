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
@Controller
@RequestMapping("/student/profile")
public class ProfileController {

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

    // 학생 프로필 수정 폼
    @GetMapping("/{userId}/edit")
    public String editProfile(@PathVariable String userId, Model model) {
        try {
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
                                @RequestParam String email,
                                @RequestParam String github,
                                @RequestParam String blog,
                                @RequestParam(required = false) MultipartFile readmeFile,
                                @RequestParam(required = false) MultipartFile profileImage,
                                Model model) throws IOException {
        try {
            String profileImagePath = null;
            String readmeFilePath = null;

            if (profileImage != null && !profileImage.isEmpty()) {
                String uploadDir = "C:/upload/profile/";
                String fileName = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();
                File saveFile = new File(uploadDir, fileName);
                File parentDir = saveFile.getParentFile();
                if (!parentDir.exists()) {
                    boolean created = parentDir.mkdirs();
                    if (!created) {
                        throw new IOException("업로드 폴더 생성 실패: " + parentDir.getAbsolutePath());
                    }
                }

                profileImage.transferTo(saveFile);
                profileImagePath = "student/profile-images/" + fileName;
            }
            if(readmeFile != null && !readmeFile.isEmpty()) {
                String uploadDir = "C:/upload/profile/";
                String fileName = UUID.randomUUID() + "_" + readmeFile.getOriginalFilename();
                File saveFile = new File(uploadDir, fileName);
                saveFile.getParentFile().mkdirs();
                readmeFile.transferTo(saveFile);
                readmeFilePath = "student/markdown/" + fileName;
            }

            profileService.updateProfile(userId, email, github, blog, readmeFilePath, profileImagePath);

            // 수정 후 해당 학생 상세 페이지로 리다이렉트
            return "redirect:/student/profile/" + userId;
        } catch (Exception e) {
            log.error("Error in updateProfile: ", e);
            throw e;
        }
    }
}
