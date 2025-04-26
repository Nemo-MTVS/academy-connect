// package store.mtvs.academyconnect.global.config;
//
// import jakarta.annotation.PostConstruct;
// import lombok.RequiredArgsConstructor;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.transaction.annotation.Transactional;
// import store.mtvs.academyconnect.classgroup.domain.entity.ClassGroup;
// import store.mtvs.academyconnect.classgroup.infrastructure.repository.ClassGroupRepository;
//
// import java.time.Clock;
// import java.time.LocalDateTime;
// import java.util.List;
//
// @Configuration
// @RequiredArgsConstructor
// public class ClassGroupConfiguration {
//
//     private final ClassGroupRepository classGroupRepository;
//     private final Clock clock;
//
//     private static final List<String> DEFAULT_CLASS_GROUPS = List.of("백엔드", "유니티", "TA");
//
//     @PostConstruct
//     @Transactional
//     public void initClassGroups() {
//         LocalDateTime now = LocalDateTime.now(clock);
//
//         for (String name : DEFAULT_CLASS_GROUPS) {
//             boolean exists = classGroupRepository.existsByName(name);
//             if (!exists) {
//                 ClassGroup group = new ClassGroup(
//                         name,
//                         now,
//                         now.plusMonths(10) // 만료일 대충 10년 뒤로 설정
//                 );
//                 classGroupRepository.save(group);
//             }
//         }
//     }
// }
