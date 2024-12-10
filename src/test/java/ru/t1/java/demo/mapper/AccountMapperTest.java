package ru.t1.java.demo.mapper;

import org.junit.jupiter.api.Test;
import ru.t1.java.demo.emums.AccountStatus;
import ru.t1.java.demo.emums.AccountType;
import ru.t1.java.demo.entity.AccountEntity;
import ru.t1.java.demo.entity.ClientEntity;
import ru.t1.java.demo.model.reqest.AccountRequest;
import ru.t1.java.demo.model.response.AccountResponse;

import static org.junit.jupiter.api.Assertions.*;

class AccountMapperTest {
    private final AccountMapper mapper = AccountMapper.ACCOUNT_MAPPER;

    @Test
    void shouldMapEntityToResponseCorrectly() {
        ClientEntity client = new ClientEntity();
        client.setClientId(1L);

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(100L);
        accountEntity.setClient(client);
        accountEntity.setAccountId("123");
        accountEntity.setAccountType(AccountType.DEBIT);
        accountEntity.setStatus(AccountStatus.OPEN);
        accountEntity.setBalance(5000L);
        accountEntity.setFrozenAmount(200L);

        AccountResponse response = mapper.toResponse(accountEntity);

        assertNotNull(response, "Response should not be null");
        assertEquals(100L, response.id());
        assertEquals(1L, response.clientId());
        assertEquals("123", response.accountId());
        assertEquals("DEBIT", response.accountType());
        assertEquals("OPEN", response.status());
        assertEquals(5000L, response.balance());
        assertEquals(200L, response.frozenAmount());
    }

    @Test
    void shouldMapRequestToEntityCorrectly() {
        AccountRequest accountRequest = new AccountRequest(
                1L,
                "DEBIT",
                "OPEN",
                5000L,
                200L
        );

        ClientEntity client = new ClientEntity();
        client.setClientId(1L);

        AccountEntity entity = mapper.toEntity(accountRequest, client);

        assertNotNull(entity, "Entity should not be null");
        assertEquals(client, entity.getClient());
        assertNull(entity.getId(), "Id should be null for new entity");
        assertEquals(AccountType.DEBIT, entity.getAccountType());
        assertEquals(AccountStatus.OPEN, entity.getStatus());
        assertEquals(5000L, entity.getBalance());
        assertEquals(200L, entity.getFrozenAmount());
    }

    @Test
    void shouldHandleNullClientGracefully() {
        AccountRequest accountRequest = new AccountRequest(
                null,
                "DEBIT",
                "OPEN",
                5000L,
                200L
        );

        AccountEntity entity = mapper.toEntity(accountRequest, null);

        assertNotNull(entity, "Entity should not be null");
        assertNull(entity.getClient(), "Client should be null");
        assertEquals(AccountType.DEBIT, entity.getAccountType());
        assertEquals(AccountStatus.OPEN, entity.getStatus());
        assertEquals(5000L, entity.getBalance());
        assertEquals(200L, entity.getFrozenAmount());
    }
}