package ru.t1.java.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.t1.java.demo.dao.persistence.AccountRepository;
import ru.t1.java.demo.dao.persistence.ClientRepository;
import ru.t1.java.demo.emums.AccountStatus;
import ru.t1.java.demo.emums.AccountType;
import ru.t1.java.demo.entity.AccountEntity;
import ru.t1.java.demo.entity.ClientEntity;
import ru.t1.java.demo.exception.EntityNotFoundException;
import ru.t1.java.demo.model.reqest.AccountRequest;
import ru.t1.java.demo.model.response.AccountResponse;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private ClientRepository clientRepository;
    @InjectMocks
    private AccountService accountService;


    @Test
    void shouldDeleteAccountSuccessfully() {
        accountService.deleteAccount(1L);
        verify(accountRepository).deleteById(1L);
    }

    @Test
    void shouldNotFailIfAccountDoesNotExist() {
        doNothing().when(accountRepository).deleteById(1L);
        accountService.deleteAccount(1L);
        verify(accountRepository).deleteById(1L);
    }

    @Test
    void shouldReturnAccountByIdIfExists() {
        AccountEntity account = new AccountEntity();
        account.setId(1L);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        AccountResponse response = accountService.getAccountById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
    }

    @Test
    void shouldThrowExceptionIfAccountNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> accountService.getAccountById(1L));
    }

    @Test
    void shouldCreateAccountSuccessfully() {
        ClientEntity client = new ClientEntity();
        client.setClientId(1L);
        client.setBlocked(false);

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        accountEntity.setClient(client);
        accountEntity.setAccountType(AccountType.DEBIT);
        accountEntity.setStatus(AccountStatus.OPEN);
        accountEntity.setBalance(1000L);
        accountEntity.setFrozenAmount(0L);

        AccountRequest accountRequest = new AccountRequest(
                1L,
                "DEBIT",
                "OPEN",
                1000L,
                0L
        );

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(accountRepository.save(any(AccountEntity.class))).thenReturn(accountEntity);

        AccountResponse response = accountService.createdAccount(accountRequest);

        assertNotNull(response, "Response should not be null");
        assertEquals(1L, response.clientId());
        assertEquals("DEBIT", response.accountType());
        assertEquals("OPEN", response.status());
        assertEquals(1000L, response.balance());
        assertEquals(0L, response.frozenAmount());

        verify(accountRepository).save(any(AccountEntity.class));
    }

    @Test
    void shouldThrowExceptionIfClientNotFound() {
        AccountRequest accountRequest = new AccountRequest(
                1L,
                "DEBIT",
                "OPEN",
                1000L,
                0L
        );
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> accountService.createdAccount(accountRequest));
    }

    @Test
    void shouldCreateBlockedAccountIfClientIsBlocked() {
        ClientEntity client = new ClientEntity();
        client.setClientId(1L);
        client.setBlocked(true);

        AccountRequest accountRequest = new AccountRequest(
                1L,
                "DEBIT",
                "OPEN",
                1000L,
                0L
        );
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(accountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AccountResponse response = accountService.createdAccount(accountRequest);

        assertNotNull(response);
        assertEquals("BLOCKED", response.status());
        verify(accountRepository).save(any(AccountEntity.class));
    }

    @Test
    void shouldReturnPageOfAccounts() {
        AccountEntity account1 = new AccountEntity();
        account1.setId(1L);

        AccountEntity account2 = new AccountEntity();
        account2.setId(2L);

        List<AccountEntity> accounts = List.of(account1, account2);
        Page<AccountEntity> accountPage = new PageImpl<>(accounts);

        when(accountRepository.findAll(any(Pageable.class))).thenReturn(accountPage);

        Page<AccountResponse> responsePage = accountService.getAllAccounts(Pageable.ofSize(10));

        assertNotNull(responsePage);
        assertEquals(2, responsePage.getTotalElements());
        assertEquals(1L, responsePage.getContent().get(0).id());
        assertEquals(2L, responsePage.getContent().get(1).id());
    }

    @Test
    void shouldReturnEmptyPageIfNoAccounts() {
        Page<AccountEntity> emptyPage = new PageImpl<>(Collections.emptyList());
        when(accountRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        Page<AccountResponse> responsePage = accountService.getAllAccounts(Pageable.ofSize(10));

        assertNotNull(responsePage);
        assertEquals(0, responsePage.getTotalElements());
        assertTrue(responsePage.getContent().isEmpty());
    }

}