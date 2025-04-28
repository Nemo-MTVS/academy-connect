package store.mtvs.academyconnect.user.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.mtvs.academyconnect.user.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    @Query("SELECT u FROM User u JOIN FETCH u.classGroup WHERE u.loginId = :loginId")
    Optional<User> findByLoginId(@Param("loginId") String loginId);

    @Query("""
        SELECT u FROM User u
                JOIN FETCH u.classGroup
                JOIN FETCH Profile p on u.id = p.id WHERE u.role = :role
    """)
    List<User> findByRoleWithClassGroupWithProfile(String role);
}