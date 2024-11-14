package ru.t1.java.service2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.service2.entity.ClientEntity;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

}