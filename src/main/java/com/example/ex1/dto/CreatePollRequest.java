package com.example.ex1.dto;

import java.time.Instant;

public class CreatePollRequest {
    public int userID;
    public String question;
    public Instant publishedAt;
    public Instant validUntil;
}
