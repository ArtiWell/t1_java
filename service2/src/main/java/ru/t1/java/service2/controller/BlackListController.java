package ru.t1.java.service2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.service2.service.BlacklistService;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class BlackListController {

    private final BlacklistService service;

    @GetMapping("check")
    public boolean checkBlackList (@RequestParam Long id) {
        return service.checkBlackList(id);
    }





}
