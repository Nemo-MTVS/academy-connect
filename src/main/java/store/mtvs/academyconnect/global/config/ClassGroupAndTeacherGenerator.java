package store.mtvs.academyconnect.global.config;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import store.mtvs.academyconnect.classgroup.domain.entity.ClassGroup;
import store.mtvs.academyconnect.classgroup.infrastructure.repository.ClassGroupRepository;
import store.mtvs.academyconnect.lunchmatching.domain.entity.LunchMatchingClass;
import store.mtvs.academyconnect.lunchmatching.infrastructure.repository.LunchMatchingClassRepository;
import store.mtvs.academyconnect.profile.domain.entity.Profile;
import store.mtvs.academyconnect.user.domain.entity.User;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ClassGroupAndTeacherGenerator {
    private final ClassGroupRepository classGroupRepository;
    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;
    private final LunchMatchingClassRepository lunchMatchingClassRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        initClassGroups();
        initTeachers();
        initLunchMatchingClasses();
    }

    private void initClassGroups() {
        List<String> groups = List.of("Backend", "Unity", "TA");
        LocalDateTime now = LocalDateTime.now(clock);

        for (String name : groups) {
            if (!classGroupRepository.existsByName(name)) {
                ClassGroup group = new ClassGroup(name, now, now.plusMonths(10));
                classGroupRepository.save(group);
            }
        }
    }

    private void initTeachers() {
        createTeacherWithProfile(
                "uuid-be-ins", "Backend", "login_be_ins", "1", "이상우",
                "스스로의 가능성에 도전해보세요.여러분의 성장과 도전을 응원하겠습니다.",
                "https://github.com/be-instructor", "https://blog.com/be-instructor", "be-ins@example.com",
                "https://d3ar1j782m1xhz.cloudfront.net/SITE_00001/teacher/20250122132941_%EC%9D%B4%EC%83%81%EC%9A%B0%20%EC%9D%B4%EB%A0%A5%EC%84%9C%EC%82%AC%EC%A7%84.jpg"
        );

        createTeacherWithProfile(
                "uuid-unity-ins", "Unity", "login_unity_ins", "1", "오민석",
                "메타버스 콘텐츠 개발자 양성을 위해 최선을 다 하겠습니다.",
                "https://github.com/unity-instructor", "https://blog.com/unity-instructor", "unity-ins@example.com",
                "https://d3ar1j782m1xhz.cloudfront.net/SITE_00001/teacher/20250122132456_%EC%82%AC%EC%A7%84.jpg"
        );

        createTeacherWithProfile(
                "uuid-ta-ins", "TA", "login_ta_ins", "1", "최용훈",
                "3D모델링과 3D스캐닝 기술을 활용하여 메타버스 플렛폼 및 적용 가능한 소스를 제작하는데 도움이 되고 싶습니다.",
                "https://github.com/ta-instructor", "https://blog.com/ta-instructor", "ta-ins@example.com",
                "https://d3ar1j782m1xhz.cloudfront.net/SITE_00001/teacher/20250122132257_2024_%EC%A6%9D%EB%AA%85%EC%82%AC%EC%A7%84.jpg"
        );
    }

    private void createTeacherWithProfile(
            String id, String classGroupName, String loginId, String rawPassword, String name,
            String md, String github, String blog, String email, String filePath
    ) {
        if (entityManager.find(User.class, id) != null) return;

        ClassGroup classGroup = classGroupRepository.findByName(classGroupName)
                .orElseThrow(() -> new IllegalStateException("ClassGroup \"" + classGroupName + "\" 없음"));

        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(id, classGroup, loginId, encodedPassword, name, "TEACHER");

        entityManager.persist(user);   // 반드시 먼저 저장
        entityManager.flush();         // flush 해서 ID가 반영되게

        Profile profile = new Profile(md, github, blog, email, filePath, user);
        entityManager.persist(profile);
    }

    private void initLunchMatchingClasses() {
        List<LunchMatchingClass> defaultClasses = List.of(
                new LunchMatchingClass(1L, nowAsString(), "TA-BE"),
                new LunchMatchingClass(2L, nowAsString(), "BE-Unity"),
                new LunchMatchingClass(3L, nowAsString(), "Unity-TA")
        );

        for (LunchMatchingClass lunchMatchingClass : defaultClasses) {
            if (!lunchMatchingClassRepository.existsById(lunchMatchingClass.getId())) {
                lunchMatchingClassRepository.save(lunchMatchingClass);
            }
        }
    }

    private String nowAsString() {
        return LocalDateTime.now(clock).toString(); // "2025-05-01T15:30:00" 같은 형식
    }
}
