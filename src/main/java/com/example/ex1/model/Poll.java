package com.example.ex1.model;

import java.time.Instant;

public class Poll {
    public int pollID;
    public int userID;
    public String question;
    public Instant publishedAt;
    public Instant validUntil;

    public Poll(int pollID, int userID, String question, Instant publishedAt, Instant validUntil) {
        this.pollID = pollID;
        this.userID = userID;
        this.question = question;
        this.publishedAt = publishedAt;
        this.validUntil = validUntil;
    }
}
