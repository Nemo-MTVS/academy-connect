package store.mtvs.academyconnect.consulting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.mtvs.academyconnect.consulting.dto.TeacherInfoForListDto;
import store.mtvs.academyconnect.consulting.dto.TeacherProfileDto;
import store.mtvs.academyconnect.profile.domain.entity.Profile;
import store.mtvs.academyconnect.profile.infrastructure.repository.ProfileRepository;
import store.mtvs.academyconnect.user.domain.entity.User;
import store.mtvs.academyconnect.user.infrastructure.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User 도메인과 연동하는 서비스 클래스
 * 실제로는 User 패키지에 있어야 하지만, User담당자와 이야기 전에 일단 테스트 구현 위해 consulting 패키지에 생성함
 * 이야기 후 리팩토링 예정
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstructorInfoService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    /**
     * 활성화된 모든 강사 목록 조회
     */
    public List<TeacherInfoForListDto> getActiveTeachers() {
        List<User> instructors = userRepository.findByRoleWithClassGroupWithProfile("INSTRUCTOR");
        
        return instructors.stream()
                .filter(instructor -> instructor.getDeletedAt() == null)
                .map(instructor -> {
                    Profile profile = instructor.getProfile();
                    String profileImageUrl = profile != null ? profile.getFilePath() : null;
                    
                    return TeacherInfoForListDto.builder()
                            .id(instructor.getId())
                            .name(instructor.getName())
                            .classGroupName(instructor.getClassGroup().getName())
                            .profileImageUrl(profileImageUrl)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 특정 강사의 프로필 정보 조회
     */
    public TeacherProfileDto getTeacherProfile(String instructorId) {
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("강사를 찾을 수 없습니다."));
        
        if (instructor.getDeletedAt() != null || !"INSTRUCTOR".equals(instructor.getRole())) {
            throw new IllegalArgumentException("유효하지 않은 강사입니다.");
        }
        
        Profile profile = profileRepository.findById(instructorId)
                .orElse(null);
                
        return TeacherProfileDto.builder()
                .id(instructor.getId())
                .name(instructor.getName())
                .classGroupName(instructor.getClassGroup().getName())
                .bio(profile != null ? profile.getMd() : null)
                .profileImageUrl(profile != null ? profile.getFilePath() : null)
                //.github(profile != null ? profile.getGithub() : null)
                //.blog(profile != null ? profile.getBlog() : null)
                //.email(profile != null ? profile.getEmail() : null)
                .build();
    }
    
    /**
     * 학생에게 할당된 선생님 ID 조회 (기본 강사)
     * 실제로는 학생의 반 정보를 이용해 담당 강사를 조회해야 하지만,
     * 지금은 간단하게 첫 번째 강사를 반환
     */
    public String getAssignedTeacherId(String studentId) {
        List<User> instructors = userRepository.findByRoleWithClassGroupWithProfile("INSTRUCTOR");
        if (!instructors.isEmpty()) {
            return instructors.get(0).getId();
        }
        return null;
    }

    /**
     * 특정 ID의 User 조회
     */
    public User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}