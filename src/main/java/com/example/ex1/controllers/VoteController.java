package com.example.ex1.controllers;

import com.example.ex1.dto.SubmitVoteRequest;
import com.example.ex1.model.User;
import com.example.ex1.model.Vote;
import com.example.ex1.repo.PollManager;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

@RestController
public class VoteController {
    private final PollManager manager;

    public VoteController() {
        this.manager = new PollManager();
    }

    @PostMapping("/vote")
    public Vote createVote(@RequestBody SubmitVoteRequest r) {
        return manager.createVote(r.userId, r.voteOptionID, r.publishedAt);
    }

    @PostMapping("/vote/replace")
    public Vote replaceVote(@RequestBody SubmitVoteRequest r) {
        return manager.replaceVote(r.userId, r.voteOptionID, r.publishedAt);
    }

    @GetMapping("/vote/all")
    public List<Vote> showAllVotes() {
        return manager.showAllVotes();
    }
}
