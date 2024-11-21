package ru.t1.java.service2.service;

import org.springframework.stereotype.Service;

@Service
public class BlacklistService {


    public boolean checkBlackList(Long id) {
        return Math.random() > 0.5;
    }
}
