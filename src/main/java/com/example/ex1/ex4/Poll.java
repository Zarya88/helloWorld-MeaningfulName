package com.example.ex1.ex4;

import jakarta.persistence.*;
import java.util.ArrayList; import java.util.List;

@Entity(name="Poll")
public class Poll {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String question;

    @ManyToOne(optional=false) @JoinColumn(name="created_by_id")
    private User createdBy;

    @OneToMany(mappedBy="poll", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<VoteOption> options = new ArrayList<>();

    protected Poll() {}
    public Poll(String question, User createdBy){ this.question=question; this.createdBy=createdBy; }

    public VoteOption addVoteOption(String caption){
        VoteOption o = new VoteOption(caption, this, options.size());
        options.add(o);
        return o;
    }

    public User getCreatedBy(){ return createdBy; }
    public List<VoteOption> getOptions(){ return options; }
}
