package com.example.ex1.ex4;

public class VoteOption {
    private final String caption;
    private final int presentationOrder; // 0-based, used in tests
    private final Poll poll;

    VoteOption(String caption, Poll poll, int presentationOrder) {
        this.caption = caption;
        this.poll = poll;
        this.presentationOrder = presentationOrder;
    }

    // getters used by queries/tests
    public String getCaption() { return caption; }
    public int getPresentationOrder() { return presentationOrder; }
    public Poll getPoll() { return poll; }
}
