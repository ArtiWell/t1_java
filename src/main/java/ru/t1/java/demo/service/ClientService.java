package ru.t1.java.demo.service;

import ru.t1.java.demo.entity.ClientEntity;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.model.reqest.ClientRequest;
import ru.t1.java.demo.model.response.ClientResponse;

import java.io.IOException;
import java.util.List;

public interface ClientService {
    List<ClientEntity> parseJson() throws IOException;

    ClientDto createdClient(ClientRequest clientRequest);
}
