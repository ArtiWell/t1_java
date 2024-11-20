package ru.t1.java.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.entity.ClientEntity;
import ru.t1.java.demo.exception.EntityNotFoundException;
import ru.t1.java.demo.mapper.ClientMapper;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.repository.ClientRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class LegacyClientService {
    private final ClientRepository repository;
    private final Map<Long, ClientEntity> cache;

    public LegacyClientService(ClientRepository repository) {
        this.repository = repository;
        this.cache = new HashMap<>();
    }

//    @PostConstruct
//    void init() {
//        getClient(1L);
//    }

    public ClientDto getClient(Long id) {
        log.debug("Call method getClient with id {}", id);
        ClientDto clientDto = null;

        if (cache.containsKey(id)) {
            return ClientMapper.toDto(cache.get(id));
        }

        try {
            ClientEntity entity = repository.findById(id).orElseThrow(
                    () -> new EntityNotFoundException("Client not found with id " + id)
            );
            clientDto = ClientMapper.toDto(entity);
            cache.put(id, entity);
        } catch (Exception e) {
            log.error("Error: ", e);
//            throw new ClientException();
        }

//        log.debug("Client info: {}", clientDto.toString());
        return clientDto;
    }

}
