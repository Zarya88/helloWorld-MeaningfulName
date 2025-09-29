package com.example.ex1.controllers;

import com.example.ex1.dto.SubmitVoteRequest;
import com.example.ex1.model.Vote;
import com.example.ex1.repo.OLD.OLDPollManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class VoteController {
    private final OLDPollManager manager;

    public VoteController(OLDPollManager manager) {
        this.manager = manager;
    }

    @GetMapping("/vote/all")
    public List<Vote> showAllVotes() {
        return manager.showAllVotes();
    }

    @PostMapping("/poll/{id}/vote")
    public Map<String, Object> createVote(@PathVariable int id,
                                          @RequestBody SubmitVoteRequest r) {
        manager.createVote(r.userId, r.voteOptionID, r.publishedAt);
        var view = manager.pollView(id);
        if (view == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        return view;
    }

    @PostMapping("/poll/{id}/replace")
    public java.util.Map<String, Object> replaceVote(
            @PathVariable int id,
            @RequestBody SubmitVoteRequest r) {

        manager.replaceVote(r.userId, r.voteOptionID, r.publishedAt);

        // return an updated snapshot of the poll
        var view = manager.pollView(id);
        if (view == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        return view;
    }
}
