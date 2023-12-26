package com.vicarius.ratelimiter.repository;

import com.vicarius.ratelimiter.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> findByIdAndDisabled(UUID id, boolean disabled);

    List<User> findAllByDisabled(boolean disabled);
}
