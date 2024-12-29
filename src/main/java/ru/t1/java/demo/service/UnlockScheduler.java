package ru.t1.java.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.t1.java.demo.dao.persistence.AccountRepository;
import ru.t1.java.demo.dao.persistence.ClientRepository;
import ru.t1.java.demo.emums.AccountStatus;
import ru.t1.java.demo.entity.AccountEntity;
import ru.t1.java.demo.entity.ClientEntity;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnlockScheduler {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final RestClient restClient;

    @Value("${unlock.scheduler.clients.count}")
    private int clientCount;

    @Value("${unlock.scheduler.accounts.count}")
    private int accountCount;

    @Value("${service3.url}")
    private String service3Url;

    @Scheduled(fixedRateString = "${unlock.scheduler.clients.period}")
    public void processClientUnlockRequests() {
        List<ClientEntity> blockedClients = clientRepository.findTopBlockedClients(clientCount);
        log.info("Processing unlock requests for {} clients", blockedClients.size());

        blockedClients.forEach(client -> {
            boolean result = restClient.post()
                    .uri(service3Url + "/api/unblock/client/" + client.getId())
                    .retrieve()
                    .body(Boolean.class);

            if (result) {
                client.setBlocked(false);
                clientRepository.save(client);
                log.info("Client {} successfully unlocked", client.getId());
            } else {
                log.info("Client {} remains blocked", client.getId());
            }
        });
    }

    @Scheduled(fixedRateString = "${unlock.scheduler.accounts.period}")
    public void processAccountUnlockRequests() {
        List<AccountEntity> arrestedAccounts = accountRepository.findTopArrestedAccounts(accountCount);
        log.info("Processing unlock requests for {} accounts", arrestedAccounts.size());

        arrestedAccounts.forEach(account -> {
            boolean result = restClient.post()
                    .uri(service3Url + "/api/unblock/account/" + account.getId())
                    .retrieve()
                    .body(Boolean.class);

            if (result) {
                account.setStatus(AccountStatus.OPEN);
                accountRepository.save(account);
                log.info("Account {} successfully unblocked", account.getId());
            } else {
                log.info("Account {} remains arrested", account.getId());
            }
        });
    }
}