package com.example.ex1.controllers;
import com.example.ex1.dto.CreatePollRequest;
import com.example.ex1.model.Poll;
import com.example.ex1.repo.PollManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin
public class PollController {
    private final PollManager manager;

    public PollController() {
        this.manager = new PollManager();
    }

    @PostMapping("/poll/create")
    public Poll createPoll(@RequestBody CreatePollRequest r) {
        return manager.createPoll(r.userID, r.question, r.publishedAt, r.validUntil, r.options);
    }

    @GetMapping("/poll/all")
    public List<Poll> getPolls() {
        return manager.getPolls();
    }

    @GetMapping("/poll/{pollId}")
    public Poll getPoll(@PathVariable int pollId) {
        Poll p = manager.listPolls.get(pollId);
        if (p == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        }
        return p;
    }

    @DeleteMapping("/poll/{pollId}")
    public void deletePoll(@PathVariable int pollId) {
        manager.deletePoll(pollId);
    }


}
