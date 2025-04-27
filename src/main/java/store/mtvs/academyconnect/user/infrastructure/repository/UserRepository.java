package store.mtvs.academyconnect.user.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import store.mtvs.academyconnect.user.domain.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByLoginId(String loginId);
    
    @Query("SELECT u FROM User u JOIN FETCH u.classGroup c WHERE c.name = :className")
    List<User> findByClassGroupName(@Param("className") String className);
} 