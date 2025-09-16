package com.example.ex1.ex4;

import jakarta.persistence.*;

@Entity(name="VoteOption")
public class VoteOption {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String caption;
    @Column(nullable=false) private int presentationOrder;

    @ManyToOne(optional=false) @JoinColumn(name="poll_id")
    private Poll poll;

    protected VoteOption() {}
    VoteOption(String caption, Poll poll, int order){
        this.caption=caption; this.poll=poll; this.presentationOrder=order;
    }

    public String getCaption(){ return caption; }
    public int getPresentationOrder(){ return presentationOrder; }
    public Poll getPoll(){ return poll; }
}
