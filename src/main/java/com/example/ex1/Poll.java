package com.example.ex1;

import org.springframework.web.bind.annotation.*;

import java.time.Instant;

public class Poll {
    String question;
    Instant publishedAt;
    Instant validUntil;

    public Poll(String question, Instant publishedAt, Instant validUntil) {
        this.question = question;
        this.publishedAt = publishedAt;
        this.validUntil = validUntil;
    }
}
