package com.vicarius.ratelimiter.repository;

import com.vicarius.ratelimiter.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
}
