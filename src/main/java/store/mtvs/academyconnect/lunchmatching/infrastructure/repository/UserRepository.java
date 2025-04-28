package store.mtvs.academyconnect.lunchmatching.infrastructure.repository;

import store.mtvs.academyconnect.user.domain.entity.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(String id);
}
