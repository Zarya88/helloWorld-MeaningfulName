package com.example.ex1.model;

import java.time.Instant;

public class Vote {
    public int voteID;
    public Instant publishedAt;
    public int userId;
    public int voteOptionID;

    public Vote(int userID, int voteID, int voteOptionID, Instant publishedAt){
        this.userId = userID;
        this.voteID  = voteID;
        this.voteOptionID = voteOptionID;
        this.publishedAt = publishedAt;

    }

}
