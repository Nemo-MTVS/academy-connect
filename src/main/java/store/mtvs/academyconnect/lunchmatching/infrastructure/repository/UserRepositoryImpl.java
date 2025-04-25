package store.mtvs.academyconnect.lunchmatching.infrastructure.repository;

import org.springframework.stereotype.Repository;
import store.mtvs.academyconnect.classgroup.domain.entity.ClassGroup;
import store.mtvs.academyconnect.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    /**
     * 실제 DB가 아닌, Mock 데이터를 기반으로 사용자 정보를 반환함
     * 테스트 또는 개발 초기 단계에서 사용됨
     */
    @Override
    public Optional<User> findById(String id) {
        // 전공 정보 생성 (BE, U)
        ClassGroup be = ClassGroup.builder()
                .id(1L)
                .name("BE")
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMonths(1))
                .build();

        ClassGroup unity = ClassGroup.builder()
                .id(2L)
                .name("U")
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMonths(1))
                .build();

        // 사용자 ID에 따라 조건 분기하여 Mock 유저 반환
        switch (id) {
            // BE 전공 수강생들
            case "uuid-be-001":
            case "uuid-be-003":
            case "uuid-be-004":
            case "uuid-be-005":
            case "uuid-be-006":
            case "uuid-be-007":
                return Optional.of(User.builder()
                        .id(id)
                        .loginId("login_be_001")
                        .password("pass")
                        .name("홍길동1")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .deletedAt(null)
                        .role("STUDENT")
                        .classGroup(be)
                        .build());
            // 강사 계정
            case "uuid-instructor":
                return Optional.of(User.builder()
                        .id(id)
                        .loginId("login_instructor")
                        .password("pass")
                        .name("강사")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .deletedAt(null)
                        .role("INSTRUCTOR")
                        .classGroup(be)
                        .build());

            // 탈퇴한 유저 (deletedAt  값이 존재)
            case "uuid-deleted":
                return Optional.of(User.builder()
                        .id(id)
                        .loginId("login_deleted")
                        .password("pass")
                        .name("삭제유저")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .deletedAt(LocalDateTime.now())
                        .role("STUDENT")
                        .classGroup(be)
                        .build());

            // U 전공 수강생
            case "uuid-unity-001":
                return Optional.of(User.builder()
                        .id(id)
                        .loginId("login_unity")
                        .password("pass")
                        .name("임꺽정")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .deletedAt(null)
                        .role("STUDENT")
                        .classGroup(unity)
                        .build());

            // 그 외: 사용자 없음
            default:
                return Optional.empty();
        }
    }
}



