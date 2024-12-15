package com.example.demo.Repositories;

import com.example.demo.Entities.Roles;
import com.example.demo.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByLogin(String login);
    User findFirstByRole(Roles role);
}
