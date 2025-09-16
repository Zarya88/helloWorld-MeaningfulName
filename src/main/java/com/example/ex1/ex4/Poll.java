package com.example.ex1.ex4;

import java.util.ArrayList;
import java.util.List;

public class Poll {
    private final String question;
    private final User createdBy;
    private final List<VoteOption> options = new ArrayList<>();

    public Poll(String question, User createdBy) {
        this.question = question;
        this.createdBy = createdBy;
    }

    /** Adds an option; its presentationOrder is the index at insertion time (0,1,2,...) */
    public VoteOption addVoteOption(String caption) {
        VoteOption opt = new VoteOption(caption, this, options.size());
        options.add(opt);
        return opt;
    }

    // getters used by queries/tests
    public String getQuestion() { return question; }
    public User getCreatedBy() { return createdBy; }
    public List<VoteOption> getOptions() { return options; }
}
