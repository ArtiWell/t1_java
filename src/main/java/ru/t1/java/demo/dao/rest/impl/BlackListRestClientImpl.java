package ru.t1.java.demo.dao.rest.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.t1.java.demo.dao.rest.BlackListRestClient;
import ru.t1.java.demo.model.response.BlackListResponse;

@Component
@RequiredArgsConstructor
public class BlackListRestClientImpl implements BlackListRestClient {

    private final RestClient restClient;

    @Override
    public boolean isBlocked(Long id) {
        BlackListResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("check")
                        .queryParam("id", id)
                        .build())
                .retrieve()
                .body(BlackListResponse.class);
        return response != null && response.blocked();
    }
}
