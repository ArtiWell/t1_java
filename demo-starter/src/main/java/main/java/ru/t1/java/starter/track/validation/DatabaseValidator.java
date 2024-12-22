package main.java.ru.t1.java.starter.track.validation;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
@Component
public class DatabaseValidator {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void validateDatabaseSchema() {
        try (Connection connection = dataSource.getConnection();
             ResultSet tables = connection.getMetaData().getTables(null, null, "DATA_SOURCE_ERROR_LOG", null)) {
            if (!tables.next()) {
                log.info("Creating missing table DATA_SOURCE_ERROR_LOG...");
                try (Statement statement = connection.createStatement()) {
                    statement.execute("CREATE TABLE DATA_SOURCE_ERROR_LOG (id BIGSERIAL PRIMARY KEY, message TEXT)");
                }
            } else {
                log.info("Table DATA_SOURCE_ERROR_LOG already exists.");
            }
        } catch (SQLException e) {
            log.error("Error validating database schema: {}", e.getMessage());
        }
    }
}
