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
@CrossOrigin
public class VoteController {
    private final PollManager manager;

    public VoteController() {
        this.manager = new PollManager();
    }

    /*@PostMapping("/vote")
    public Vote createVote(@RequestBody SubmitVoteRequest r) {
        return manager.createVote(r.userId, r.voteOptionID, r.publishedAt);
    }

    @PostMapping("/vote/replace")
    public Vote replaceVote(@RequestBody SubmitVoteRequest r) {
        return manager.replaceVote(r.userId, r.voteOptionID, r.publishedAt);
    }*/

    @GetMapping("/vote/all")
    public List<Vote> showAllVotes() {
        return manager.showAllVotes();
    }

    @PostMapping("/poll/{id}/vote")
    public java.util.Map<String, Object> createVote(
            @PathVariable int id,
            @RequestBody SubmitVoteRequest r) {

        // create the vote (your existing method)
        manager.createVote(r.userId, r.voteOptionID, r.publishedAt);

        // return an updated snapshot of the poll
        var view = manager.pollView(id);
        if (view == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "Poll not found");
        }
        return view;
    }

    @PostMapping("/poll/{id}/replace")
    public java.util.Map<String, Object> replaceVote(
            @PathVariable int id,
            @RequestBody SubmitVoteRequest r) {

        // create the vote (your existing method)
        manager.replaceVote(r.userId, r.voteOptionID, r.publishedAt);

        // return an updated snapshot of the poll
        var view = manager.pollView(id);
        if (view == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "Poll not found");
        }
        return view;
    }
}
