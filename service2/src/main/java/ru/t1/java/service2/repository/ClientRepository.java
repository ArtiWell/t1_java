package ru.t1.java.service2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.service2.entity.ClientEntity;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
    Optional<ClientEntity> findByClientId(Long id);
}