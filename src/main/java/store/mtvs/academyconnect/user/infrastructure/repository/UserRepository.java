package store.mtvs.academyconnect.user.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.mtvs.academyconnect.user.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    //로그인 아이디로 유저 조회
    @Query("SELECT u FROM User u JOIN FETCH u.classGroup WHERE u.loginId = :loginId")
    Optional<User> findByLoginId(@Param("loginId") String loginId);

    //이름으로 유저 조회
    @Query("SELECT u FROM User u JOIN FETCH u.classGroup WHERE u.name = :name")
    Optional<User> findByName(@Param("name") String name);

    //이름으로 유저 목록 조회 (동명 이인 처리 용)
    @Query("SELECT u FROM User u JOIN FETCH u.classGroup WHERE u.name = :name")
    List<User> findAllByName(@Param("name") String name);

    //역할로 유저 조회
    @Query("""
        SELECT u FROM User u
                JOIN FETCH u.classGroup
                JOIN FETCH Profile p on u.id = p.id WHERE u.role = :role
    """)
    List<User> findByRoleWithClassGroupWithProfile(String role);
}