package ru.t1.java.demo.service;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;
import ru.t1.java.demo.model.reqest.TransactionRequest;
import ru.t1.java.demo.model.response.TransactionResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(WireMockExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.yml")
public class TransactionControllerIntegrationTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().dynamicPort())
            .build();

    @Autowired
    private RestClient restClient;

    @Autowired
    private TransactionService transactionService;


    @Test
    void shouldCreateTransactionSuccessfully() {
        WireMock.stubFor(post(urlEqualTo("/api/check-client"))
                .withQueryParam("clientId", equalTo("1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{ \"blocked\": false }")
                        .withHeader("Content-Type", "application/json")));
        TransactionRequest transactionRequest = new TransactionRequest(1L, 100L, "ACCEPTED");

        TransactionResponse transactionResponse = transactionService.createTransaction(transactionRequest);

        assertNotNull(transactionResponse);
        assertEquals("ACCEPTED", transactionResponse.status());

        WireMock.verify(postRequestedFor(urlEqualTo("/api/check-client"))
                .withQueryParam("clientId", equalTo("1")));
    }

    @Test
    void shouldRejectTransactionIfClientIsBlocked() {
        WireMock.stubFor(post(urlEqualTo("/api/check-client"))
                .withQueryParam("clientId", equalTo("2"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{ \"blocked\": true }")
                        .withHeader("Content-Type", "application/json")));

        TransactionRequest transactionRequest = new TransactionRequest(2L, 50L, "REJECTED");

        TransactionResponse transactionResponse = transactionService.createTransaction(transactionRequest);

        assertNotNull(transactionResponse);
        assertEquals("REJECTED", transactionResponse.status());

        WireMock.verify(postRequestedFor(urlEqualTo("/api/check-client"))
                .withQueryParam("clientId", equalTo("2")));
    }
}
