package ru.t1.java.demo.dao.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.demo.emums.TransactionStatus;
import ru.t1.java.demo.entity.AccountEntity;
import ru.t1.java.demo.entity.TransactionEntity;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {


    long countByAccountAndStatus(AccountEntity account, TransactionStatus status);
}
