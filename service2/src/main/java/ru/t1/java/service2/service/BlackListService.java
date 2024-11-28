package ru.t1.java.service2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.service2.dto.ClientDto;
import ru.t1.java.service2.entity.ClientEntity;
import ru.t1.java.service2.repository.ClientRepository;

@Service
@RequiredArgsConstructor
public class BlackListService {

    private final ClientRepository clientRepository;

    public boolean checkBlackList(Long id) {
        return clientRepository.findByClientId(id)
                .map(ClientEntity::isBlackList)
                .orElse(false);
    }

    public void saveClientStatus(ClientDto clientDto) {
        ClientEntity entity = new ClientEntity();
        entity.setClientId(clientDto.getId());
        entity.setFirstName(clientDto.getFirstName());
        entity.setLastName(clientDto.getLastName());
        entity.setMiddleName(clientDto.getMiddleName());
        entity.setBlackList(status());
        clientRepository.save(entity);
    }

    private boolean status() {
        return Math.random() > 0.5;
    }

}
