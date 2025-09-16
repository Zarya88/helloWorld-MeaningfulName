package com.example.ex1.ex4;

import jakarta.persistence.*;

@Entity(name="Vote")
public class Vote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @ManyToOne(optional=false) @JoinColumn(name="voted_by_id")
    private User votedBy;

    @ManyToOne(optional=false) @JoinColumn(name="option_id")
    private VoteOption votesOn;

    protected Vote() {}
    public Vote(User votedBy, VoteOption votesOn){ this.votedBy=votedBy; this.votesOn=votesOn; }

    public VoteOption getVotesOn(){ return votesOn; }
}
