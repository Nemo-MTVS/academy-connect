package store.mtvs.academyconnect.profile.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.mtvs.academyconnect.profile.domain.entity.Profile;
import store.mtvs.academyconnect.profile.infrastructure.repository.ProfileRepository;
import store.mtvs.academyconnect.user.domain.entity.User;
import store.mtvs.academyconnect.user.infrastructure.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public ProfileService(UserRepository userRepository, ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    // 특정 반에 소속된 학생들 조회
    public List<User> getStudentsByClassName(String className) {
        log.info("Finding students for class: {}", className);
        try {
            List<User> students = userRepository.findByClassGroupName(className);
            log.info("Found {} students in class {}", students.size(), className);
            for (User student : students) {
                log.info("Student found: {} (ID: {})", student.getName(), student.getId());
            }
            return students;
        } catch (Exception e) {
            log.error("Error finding students for class {}: {}", className, e.getMessage(), e);
            throw e;
        }
    }

    // 특정 학생의 프로필 조회
    public Profile getProfileByUserId(String userId) {
        log.info("Finding profile for user: {}", userId);
        try {
            Profile profile = profileRepository.findByUserId(userId);
            if (profile == null) {
                log.warn("No profile found for user: {}", userId);
            } else {
                log.info("Profile found for user {}: github={}, blog={}, email={}", 
                    userId, profile.getGithub(), profile.getBlog(), profile.getEmail());
            }
            return profile;
        } catch (Exception e) {
            log.error("Error finding profile for user {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    public User getUserById(String userId) {
        log.info("Finding user by id: {}", userId);
        try {
            return userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("User not found with id: {}", userId);
                        return new IllegalArgumentException("User not found");
                    });
        } catch (Exception e) {
            log.error("Error finding user {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }
}