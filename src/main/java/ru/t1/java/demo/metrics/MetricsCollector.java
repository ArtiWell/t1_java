package ru.t1.java.demo.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dao.persistence.AccountRepository;
import ru.t1.java.demo.dao.persistence.ClientRepository;
import ru.t1.java.demo.emums.AccountStatus;

@Component
@RequiredArgsConstructor
public class MetricsCollector {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final MeterRegistry meterRegistry;

    @PostConstruct
    public void initMetrics() {
        meterRegistry.gauge("blocked_clients_count", this,
                collector -> clientRepository.countByBlockedIs(true));

        meterRegistry.gauge("arrested_accounts_count", this,
                collector -> accountRepository.countByStatus(AccountStatus.ARRESTED));
    }
}