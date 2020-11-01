package com.github.mmodzel3.lostfinderserver.security;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TokenGenerator {

    public String generate() {
        return UUID.randomUUID().toString();
    }
}
