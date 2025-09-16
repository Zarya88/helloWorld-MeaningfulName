package com.example.ex1.ex4;

import java.util.LinkedHashSet;

public class User {
    private final String username;
    private final String email;
    LinkedHashSet<String> created;

    /**
     * Creates a new User object with given username and email.
     * The id of a new user object gets determined by the database.
     */
    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.created = new LinkedHashSet<>();
    }

    /**
     * Creates a new Poll object for this user
     * with the given poll question
     * and returns it.
     */
    public Poll createPoll(String question) {
        created.add(question);
        return new Poll(question, this);
    }

    /**
     * Creates a new Vote for a given VoteOption in a Poll
     * and returns the Vote as an object.
     */
    public Vote voteFor(VoteOption option) {
        return new Vote(this, option);
    }

    // getters used by queries/tests
    public String getUsername() { return username; }
    public String getEmail() { return email; }
}
