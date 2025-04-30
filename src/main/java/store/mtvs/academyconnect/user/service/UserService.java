package store.mtvs.academyconnect.user.service;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import store.mtvs.academyconnect.classgroup.domain.entity.ClassGroup;
import store.mtvs.academyconnect.classgroup.service.ClassGroupService;
import store.mtvs.academyconnect.profile.domain.entity.Profile;
import store.mtvs.academyconnect.user.domain.entity.User;
import store.mtvs.academyconnect.user.domain.enums.UserRole;
import store.mtvs.academyconnect.user.dto.InstructorDTO;
import store.mtvs.academyconnect.user.dto.UserResponseDTO;
import store.mtvs.academyconnect.user.infrastructure.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final ClassGroupService classGroupService;
    private final EntityManager entityManager; // EntityManager 주입
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(ClassGroupService classGroupService, EntityManager entityManager,
                       UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.classGroupService = classGroupService;
        this.entityManager = entityManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
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
        try{
            ClassGroup classGroup = classGroupService.getClassGroup(className);

            if (!StringUtils.hasText(loginId)
                    || !StringUtils.hasText(className)
                    || !StringUtils.hasText(password)
                    || !StringUtils.hasText(name)
            ) {
                throw new IllegalArgumentException("로그인 아이디는 필수입니다");
            }

            // 중복 아이디 체크
            if (userRepository.findByLoginId(loginId).isPresent()) {
                throw new IllegalArgumentException("이미 사용 중인 아이디입니다");
            }

            // 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(password);

            User user = new User(
                    uuid,
                    classGroup,
                    loginId,
                    encodedPassword,
                    name,
                    "STUDENT"
            );
            entityManager.persist(user);

            Profile profile = createProfile(user);
            entityManager.persist(profile);

            log.info("유저 생성 완료: 유저 ID {}", user.getId());
            return user;
        }catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    // 로그인 처리를 위한 유저 조회
    public Optional<User> getUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId);
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

    public List<InstructorDTO> getInstructorDTO() {
        List<User> users = userRepository.findByRoleWithClassGroupWithProfile(UserRole.TEACHER.name());
        if (users.isEmpty()) {
            return List.of();
        }
        return users.stream()
                .map(userMapper::toInstructorDTO)
                .toList();
    }

}
