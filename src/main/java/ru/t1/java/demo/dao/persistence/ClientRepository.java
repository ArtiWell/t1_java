package ru.t1.java.demo.dao.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.demo.entity.ClientEntity;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
}