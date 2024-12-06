package ru.t1.java.demo.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dao.rest.BlackListRestClient;
import ru.t1.java.demo.emums.AccountStatus;
import ru.t1.java.demo.exception.EntityNotFoundException;
import ru.t1.java.demo.model.reqest.AccountRequest;
import ru.t1.java.demo.entity.AccountEntity;
import ru.t1.java.demo.entity.ClientEntity;
import ru.t1.java.demo.model.response.AccountResponse;
import ru.t1.java.demo.dao.persistence.AccountRepository;
import ru.t1.java.demo.dao.persistence.ClientRepository;

import static ru.t1.java.demo.mapper.AccountMapper.ACCOUNT_MAPPER;
@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final BlackListRestClient restClient;


    public Page<AccountResponse> getAllAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable)
                .map(ACCOUNT_MAPPER::toResponse);
    }

    public AccountResponse createdAccount(AccountRequest accountRequest) {
        ClientEntity client = clientRepository.findById(accountRequest.clientId()).orElseThrow(
                ()-> new EntityNotFoundException("Client with ID "+accountRequest.clientId()+" not found")
        );
        AccountEntity account = ACCOUNT_MAPPER.toEntity(accountRequest,client);
        if (account.getClient().isBlocked()){
            account.setStatus(AccountStatus.BLOCKED);
        }
        accountRepository.save(account);
        return ACCOUNT_MAPPER.toResponse(account);
    }

    public AccountResponse getAccountById(Long id) {
        return ACCOUNT_MAPPER.toResponse(accountRepository.findById(id).orElseThrow(
                ()->new EntityNotFoundException("Account with ID "+id+" not found")
        ));
    }


    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}
