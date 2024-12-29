package ru.t1.java.demo.dao.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.t1.java.demo.emums.AccountStatus;
import ru.t1.java.demo.entity.AccountEntity;

import java.util.List;


public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    @Query("SELECT a FROM AccountEntity a WHERE a.status = 'ARRESTED' ORDER BY a.id ASC LIMIT :count")
    List<AccountEntity> findTopArrestedAccounts(@Param("count") int count);

    @Query("SELECT COUNT(a) FROM AccountEntity a WHERE a.status = 'ARRESTED'")
    int countArrestedAccounts();

    long countByStatus(AccountStatus status);
}
