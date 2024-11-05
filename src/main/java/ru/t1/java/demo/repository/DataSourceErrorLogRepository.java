package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.demo.entity.DataSourceErrorLogEntity;

public interface DataSourceErrorLogRepository extends JpaRepository<DataSourceErrorLogEntity, Long> {

}
