package com.example.ex1.dto;

import java.time.Instant;
import java.util.List;

public class CreatePollRequest {
    public int userID;
    public String question;
    public Instant publishedAt;
    public Instant validUntil;
    public List<String> options;
}
