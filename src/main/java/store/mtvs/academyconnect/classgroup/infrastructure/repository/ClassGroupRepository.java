package store.mtvs.academyconnect.classgroup.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.mtvs.academyconnect.classgroup.domain.entity.ClassGroup;

import java.util.Optional;

public interface ClassGroupRepository extends JpaRepository<ClassGroup, Long> {

    Optional<ClassGroup> findByName(String name);

    boolean existsByName(String name);
}