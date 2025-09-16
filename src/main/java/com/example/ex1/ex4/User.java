package com.example.ex1.ex4;

import jakarta.persistence.*;

@Entity(name = "User")          // so JPQL 'from User' works
@Table(name = "users")          // test runs native query on 'users'
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true) private String username;
    @Column(nullable=false, unique=true) private String email;

    protected User() {}           // JPA needs no-args ctor
    public User(String username, String email) { this.username=username; this.email=email; }

    public Poll createPoll(String question){ return new Poll(question, this); }
    public Vote voteFor(VoteOption option){ return new Vote(this, option); }

    public String getUsername(){ return username; }
    public String getEmail(){ return email; }
}
