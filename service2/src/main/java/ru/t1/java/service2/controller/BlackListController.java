package ru.t1.java.service2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.service2.dto.ClientDto;
import ru.t1.java.service2.service.BlackListService;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@Log4j2
public class BlackListController {

    private final BlackListService service;



    @PostMapping("create_client")
    public void createClientStatus(@RequestBody ClientDto clientDto) {
        service.saveClientStatus(clientDto);
    }

    @GetMapping("/check-client")
    public boolean checkClient(@RequestParam Long clientId) {
        return service.checkBlackList(clientId);
    }


}
