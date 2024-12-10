package ru.t1.java.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;
import ru.t1.java.demo.dao.persistence.AccountRepository;
import ru.t1.java.demo.dao.persistence.TransactionRepository;
import ru.t1.java.demo.emums.AccountStatus;
import ru.t1.java.demo.emums.TransactionStatus;
import ru.t1.java.demo.entity.AccountEntity;
import ru.t1.java.demo.entity.ClientEntity;
import ru.t1.java.demo.entity.TransactionEntity;
import ru.t1.java.demo.exception.EntityNotFoundException;
import ru.t1.java.demo.model.dto.TransactionMessageDTO;
import ru.t1.java.demo.model.reqest.TransactionRequest;
import ru.t1.java.demo.model.response.TransactionResponse;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private KafkaTemplate<String, TransactionMessageDTO> kafkaTemplate;
    @Mock
    private RestClient restClient;
    private TransactionService transactionService;
    @BeforeEach
    void setUp() {

        String validJwtSecret = Base64.getUrlEncoder().encodeToString("my-secret-key".getBytes(StandardCharsets.UTF_8));
        transactionService = new TransactionService(
                transactionRepository,
                accountRepository,
                kafkaTemplate,
                restClient,
                3,
                validJwtSecret,
                3600000L
        );
    }


    @Test
    void shouldArrestAccountIfRejectedTransactionsExceedThreshold() {
        AccountEntity account = new AccountEntity();
        account.setStatus(AccountStatus.OPEN);
        account.setBalance(100L);

        ClientEntity client = new ClientEntity();
        client.setBlocked(false);
        account.setClient(client);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor = ArgumentCaptor.forClass(Function.class);
        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersUriSpec).when(requestHeadersUriSpec).uri(uriCaptor.capture());
        doReturn(requestHeadersUriSpec).when(requestHeadersUriSpec).header(anyString(), anyString());
        doReturn(responseSpec).when(requestHeadersUriSpec).retrieve();
        doReturn(false).when(responseSpec).body(Boolean.class);

        when(transactionRepository.countByAccountAndStatus(account, TransactionStatus.REJECTED)).thenReturn(3L);

        TransactionRequest request = new TransactionRequest(1L, 50L, "REJECTED");

        transactionService.createTransaction(request);

        verify(accountRepository).save(account);
        assertEquals(AccountStatus.ARRESTED, account.getStatus());
    }


    @Test
    void shouldAcceptTransactionIfAccountIsOpen() {
        AccountEntity account = new AccountEntity();
        account.setStatus(AccountStatus.OPEN);
        account.setBalance(100L);

        ClientEntity client = new ClientEntity();
        client.setBlocked(false);
        account.setClient(client);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor = ArgumentCaptor.forClass(Function.class);

        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersUriSpec).when(requestHeadersUriSpec).uri(uriCaptor.capture());
        doReturn(requestHeadersUriSpec).when(requestHeadersUriSpec).header(anyString(), anyString());
        doReturn(responseSpec).when(requestHeadersUriSpec).retrieve();
        doReturn(false).when(responseSpec).body(Boolean.class);

        TransactionRequest request = new TransactionRequest(1L, 50L, "ACCEPTED");

        TransactionResponse response = transactionService.createTransaction(request);

        assertNotNull(response);
        assertEquals("ACCEPTED", response.status());
        verify(transactionRepository).save(any(TransactionEntity.class));
    }


    @Test
    void shouldRejectTransactionIfAccountIsBlocked() {
        AccountEntity account = new AccountEntity();
        ClientEntity client = new ClientEntity();
        client.setBlocked(true);
        account.setClient(client);
        account.setStatus(AccountStatus.OPEN);
        account.setBalance(100L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        TransactionRequest request = new TransactionRequest(1L, 50L, "ACCEPTED");

        TransactionResponse response = transactionService.createTransaction(request);

        assertNotNull(response);
        assertEquals("REJECTED", response.status());
        verify(transactionRepository).save(any(TransactionEntity.class));
    }


    @Test
    void shouldReturnTransactionByIdIfExists() {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setId(1L);
        transaction.setAmount(100L);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        TransactionResponse response = transactionService.getTransactionById(1L);
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(100L, response.amount());
    }

    @Test
    void shouldThrowExceptionIfTransactionNotFound() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> transactionService.getTransactionById(1L));
    }

    @Test
    void shouldReturnPageOfTransactions() {
        TransactionEntity transaction1 = new TransactionEntity();
        transaction1.setId(1L);
        transaction1.setAmount(100L);

        TransactionEntity transaction2 = new TransactionEntity();
        transaction2.setId(2L);
        transaction2.setAmount(200L);

        List<TransactionEntity> transactions = List.of(transaction1, transaction2);
        Page<TransactionEntity> transactionPage = new PageImpl<>(transactions);

        when(transactionRepository.findAll(any(Pageable.class))).thenReturn(transactionPage);

        Page<TransactionResponse> responsePage = transactionService.getAllTransaction(Pageable.ofSize(10));

        assertNotNull(responsePage);
        assertEquals(2, responsePage.getTotalElements());
        assertEquals(1L, responsePage.getContent().get(0).id());
        assertEquals(100L, responsePage.getContent().get(0).amount());
        assertEquals(2L, responsePage.getContent().get(1).id());
        assertEquals(200L, responsePage.getContent().get(1).amount());
    }

    @Test
    void shouldReturnEmptyPageIfNoTransactions() {
        Page<TransactionEntity> emptyPage = new PageImpl<>(Collections.emptyList());
        when(transactionRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        Page<TransactionResponse> responsePage = transactionService.getAllTransaction(Pageable.ofSize(10));

        assertNotNull(responsePage);
        assertEquals(0, responsePage.getTotalElements());
        assertTrue(responsePage.getContent().isEmpty());
    }

}