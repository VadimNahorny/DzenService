package com.example.dzenservice.dao;
import com.example.dzenservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByLogin(String login);
}
