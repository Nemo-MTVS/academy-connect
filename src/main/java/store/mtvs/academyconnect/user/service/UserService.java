package store.mtvs.academyconnect.user.service;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import store.mtvs.academyconnect.classgroup.domain.entity.ClassGroup;
import store.mtvs.academyconnect.classgroup.service.ClassGroupService;
import store.mtvs.academyconnect.profile.domain.entity.Profile;
import store.mtvs.academyconnect.user.domain.entity.User;
import store.mtvs.academyconnect.user.dto.UserResponseDTO;
import store.mtvs.academyconnect.user.infrastructure.repository.UserRepository;

import java.util.UUID;

@Service
public class UserService {
    private final ClassGroupService classGroupService;
    private final EntityManager entityManager; // EntityManager 주입
    private final UserRepository userRepository;

    public UserService(ClassGroupService classGroupService, EntityManager entityManager, UserRepository userRepository) {
        this.classGroupService = classGroupService;
        this.entityManager = entityManager;
        this.userRepository = userRepository;
    }

    private Profile createProfile(User user){
        return new Profile(
                "# " + user.getName() + "의 프로필입니다",
                "",
                "",
                "",
                "",
                user
        );
    }

    // 사용자 등록
    @Transactional
    public User registerUser(String loginId, String className, String password, String name) {
        String uuid = UUID.randomUUID().toString();

        ClassGroup classGroup = classGroupService.getClassGroup(className);

        User user = new User(
                uuid,
                classGroup,
                loginId,
                password,
                name,
                "STUDENT"
        );
        entityManager.persist(user);

        Profile profile = createProfile(user);
        entityManager.persist(profile);

        return user;
    }

    // 사용자 조회
    public User getUser(String userId) {
        if (!StringUtils.hasText(userId)) {
            return null;
        }
        return userRepository.findById(userId).orElse(null);
    }

    public UserResponseDTO getUserResponseDTO(String userId) {
        User user = getUser(userId);
        if (user == null) {
            return null;
        }
        return new UserResponseDTO(user.getId(), user.getName(), user.getClassGroup().getName(), user.getRole());
    }

}
