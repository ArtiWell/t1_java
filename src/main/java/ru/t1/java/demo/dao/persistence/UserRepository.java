package ru.t1.java.demo.dao.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.demo.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    Optional<UserEntity> findByLogin(String login);
}
