package ru.t1.java.demo.dao.rest.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.t1.java.demo.dao.rest.BlackListRestClient;
import ru.t1.java.demo.model.dto.ClientDto;

@Component
@RequiredArgsConstructor
public class BlackListRestClientImpl implements BlackListRestClient {

    private final RestClient restClient;

    @Override
    public void createdClient(ClientDto clientDto) {
        restClient.post()
                .uri("create_client")
                .body(clientDto)
                .retrieve()
                .toBodilessEntity();
    }
}
