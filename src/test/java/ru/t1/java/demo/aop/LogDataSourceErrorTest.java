package ru.t1.java.demo.aop;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.t1.java.demo.entity.DataSourceErrorLogEntity;
import ru.t1.java.demo.dao.persistence.DataSourceErrorLogRepository;


@SpringBootTest
@AutoConfigureMockMvc
class LogDataSourceErrorTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private DataSourceErrorLogRepository dataSourceErrorLogRepository;

    @Test
    void logDataSourceErrorInAccountController() throws Exception {
        webTestClient.get().uri("http://localhost:8080/accounts/10000").exchange().expectStatus().isBadRequest();

        Mockito.verify(dataSourceErrorLogRepository).save(Mockito.any(DataSourceErrorLogEntity.class));
    }

    @Test
    void logDataSourceErrorInTransactionController() {
        webTestClient.get().uri("http://localhost:8080/transactions/10000").exchange().expectStatus().isBadRequest();
        Mockito.verify(dataSourceErrorLogRepository).save(Mockito.any(DataSourceErrorLogEntity.class));
    }


}