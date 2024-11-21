package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dao.rest.BlackListRestClient;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.entity.ClientEntity;
import ru.t1.java.demo.model.reqest.ClientRequest;
import ru.t1.java.demo.model.response.ClientResponse;
import ru.t1.java.demo.dao.persistence.ClientRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.mapper.ClientMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository repository;
    private final BlackListRestClient restClient;

    @PostConstruct
    void init() {
        try {
            List<ClientEntity> clients = parseJson();
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
    }

    @Override
    public List<ClientEntity> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        ClientDto[] clients = mapper.readValue(new File("src/main/resources/MOCK_DATA.json"), ClientDto[].class);

        return Arrays.stream(clients)
                .map(ClientMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public ClientDto createdClient(ClientRequest clientRequest) {
        ClientEntity client = new ClientEntity();
        client.setFirstName(clientRequest.firstName());
        client.setLastName(clientRequest.lastName());
        client.setMiddleName(clientRequest.middleName());
        boolean isBlocked = restClient.isBlocked(client.getId());
        if (isBlocked) {
            client.setBlocked(true);
        }
        repository.save(client);
        return ClientMapper.toDto(client);
    }

}
