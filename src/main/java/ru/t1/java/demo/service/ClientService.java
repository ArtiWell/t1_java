package ru.t1.java.demo.service;

import ru.t1.java.demo.entity.ClientEntity;

import java.io.IOException;
import java.util.List;

public interface ClientService {
    List<ClientEntity> parseJson() throws IOException;
}
