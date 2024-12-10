package ru.t1.java.service2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.t1.java.service2.dto.ClientDto;
import ru.t1.java.service2.entity.ClientEntity;
import ru.t1.java.service2.repository.ClientRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlackListServiceTest {
    @Mock
    private ClientRepository clientRepository;
    @InjectMocks
    private BlackListService blackListService;

    @Test
    void shouldReturnTrueIfClientIsInBlackList() {
        ClientEntity client = new ClientEntity();
        client.setClientId(1L);
        client.setBlackList(true);
        when(clientRepository.findByClientId(1L)).thenReturn(Optional.of(client));

        boolean result = blackListService.checkBlackList(1L);

        assertTrue(result);
        verify(clientRepository).findByClientId(1L);
    }

    @Test
    void shouldReturnFalseIfClientIsNotInBlackList() {
        when(clientRepository.findByClientId(1L)).thenReturn(Optional.empty());

        boolean result = blackListService.checkBlackList(1L);

        assertFalse(result);
        verify(clientRepository).findByClientId(1L);
    }

    @Test
    void shouldSaveClientStatusSuccessfully() {
        ClientDto clientDto = ClientDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .middleName("Smith")
                .build();

        blackListService.saveClientStatus(clientDto);

        verify(clientRepository).save(any(ClientEntity.class));
    }

}