package ru.t1.java.demo.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.t1.java.demo.dao.persistence.AccountRepository;
import ru.t1.java.demo.emums.AccountStatus;
import ru.t1.java.demo.entity.AccountEntity;
import ru.t1.java.demo.model.reqest.AccountUnblockRequest;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountUnlockScheduler {

    private final AccountRepository accountRepository;
    private final WebClient webClient;

    @Scheduled(fixedRateString = "${unlock.accounts.rate}")
    public void unlockAccounts() {
        List<AccountEntity> arrestedAccounts = accountRepository.findTopArrestedAccounts(10);

        arrestedAccounts.forEach(account -> {
            Boolean success = webClient.post()
                    .uri("http://localhost:8083/api/unblock/accounts")
                    .bodyValue(new AccountUnblockRequest(account.getId(), "Automatic unlock"))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            if (Boolean.TRUE.equals(success)) {
                account.setStatus(AccountStatus.OPEN);
                accountRepository.save(account);
            }
        });
    }
}
