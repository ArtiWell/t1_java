package ru.t1.java.service2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.t1.java.service2.entity.TransactionEntity;

import java.time.Instant;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    @Query("SELECT t FROM TransactionEntity t WHERE t.account.client.id = :clientId " +
            "AND t.account.id = :accountId AND t.timestamp > :timeWindow")
    List<TransactionEntity> findRecentTransactions(@Param("clientId") Long clientId,
                                                   @Param("accountId") Long accountId,
                                                   @Param("timeWindow") Instant timeWindow);
}

