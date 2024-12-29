package ru.t1.java.demo.dao.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.t1.java.demo.entity.ClientEntity;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
    @Query("SELECT c FROM ClientEntity c WHERE c.isBlackList = true ORDER BY c.id ASC LIMIT :count")
    List<ClientEntity> findTopBlockedClients(@Param("count") int count);

    @Query("SELECT COUNT(c) FROM ClientEntity c WHERE c.isBlocked = true")
    int countBlockedClients();

    long countByBlockedIs(boolean blocked);
}