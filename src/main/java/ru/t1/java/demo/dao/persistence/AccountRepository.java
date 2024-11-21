package ru.t1.java.demo.dao.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.demo.entity.AccountEntity;


public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

}
