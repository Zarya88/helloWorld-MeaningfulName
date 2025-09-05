package com.example.ex1.model;

import java.time.Instant;

public class Vote {
    public int voteID;
    public Instant publishedAt;
    public int userId;
    public int voteOptionID;

    public Vote(int userID, int voteID, int voteOptionID, Instant publishedAt){
        this.publishedAt = publishedAt;
        this.userId = userID;
        this.voteOptionID = voteOptionID;
    }
}
