package com.example.ex1.model;

import java.time.Instant;

public class Vote {
    public int voteID;
    public Instant publishedAt;
    public int userId;
    public int voteOptionID;

    public Vote(int voteID, Instant publishedAt, int userID, int voteOptionID){
        this.publishedAt = publishedAt;
        this.userId = userID;
        this.voteOptionID = voteOptionID;
    }
}
