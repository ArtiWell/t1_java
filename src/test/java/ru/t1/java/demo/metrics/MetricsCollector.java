package ru.t1.java.demo.metrics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dao.persistence.AccountRepository;
import ru.t1.java.demo.dao.persistence.ClientRepository;

@Component
@RequiredArgsConstructor
public class MetricsCollector {
    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    @Gauge(name = "blocked_clients_total", description = "Total blocked clients")
    public int countBlockedClients() {
        return clientRepository.countBlockedClients();
    }

    @Gauge(name = "arrested_accounts_total", description = "Total arrested accounts")
    public int countArrestedAccounts() {
        return accountRepository.countArrestedAccounts();
    }
}
