package com.example.ex1.model;

import java.time.Instant;
import java.util.ArrayList;

public class Poll {
    public int pollID;
    public int userID;
    public String question;
    public Instant publishedAt;
    public Instant validUntil;
    public ArrayList<Integer> options;

    public Poll(int pollID, int userID, String question, Instant publishedAt, Instant validUntil, ArrayList<Integer> options) {
        this.pollID = pollID;
        this.userID = userID;
        this.question = question;
        this.publishedAt = publishedAt;
        this.validUntil = validUntil;
        this.options = options;
    }
}
