package ru.t1.java.demo.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import ru.t1.java.demo.dao.persistence.ClientRepository;
import ru.t1.java.demo.entity.ClientEntity;
import ru.t1.java.demo.model.reqest.ClientUnblockRequest;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClientUnlockScheduler {

    private final ClientRepository clientRepository;
    private final WebClient webClient;

    @Scheduled(fixedRateString = "${unlock.clients.rate}")
    public void unlockClients() {
        List<ClientEntity> blockedClients = clientRepository.findTopBlockedClients(10);

        blockedClients.forEach(client -> {
            Boolean success = webClient.post()
                    .uri("http://localhost:8083/api/unblock/clients")
                    .bodyValue(new ClientUnblockRequest(client.getId(), "Automatic unlock"))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            if (Boolean.TRUE.equals(success)) {
                client.setBlocked(false);
                clientRepository.save(client);
            }
        });
    }
}
