package com.example.ex1;

import org.springframework.web.bind.annotation.*;

public class VoteOption {
    String caption;
    int presentationOrder;
    int voteId;

    public VoteOption(String caption, int presentationOrder, int voteId) {
        this.caption = caption;
        this.presentationOrder = presentationOrder;
        this.voteId = voteId;
    }

}
