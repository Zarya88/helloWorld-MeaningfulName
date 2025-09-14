package com.example.ex1.model;

import java.util.ArrayList;

public class VoteOption {
    public int pollId;
    public String caption;
    public int presentationOrder;
    public ArrayList<Integer> voteId;
    public int voteOptionID;


    public VoteOption(int pollId, String caption, int presentationOrder, ArrayList<Integer> voteId) {
        this.pollId = pollId;
        this.caption = caption;
        this.presentationOrder = presentationOrder;
        this.voteId = voteId;
    }

}
