package com.vitor.events.repo;

import com.vitor.events.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, Integer> {
    public User findByEmail(String email);
}
