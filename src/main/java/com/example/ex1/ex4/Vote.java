package com.example.ex1.ex4;

public class Vote {
    private final User votedBy;
    private final VoteOption votesOn;

    public Vote(User votedBy, VoteOption votesOn) {
        this.votedBy = votedBy;
        this.votesOn = votesOn;
    }

    // getters used by queries/tests
    public User getVotedBy() { return votedBy; }
    public VoteOption getVotesOn() { return votesOn; }
}
