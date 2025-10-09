package com.example.ex1.controllers;
import com.example.ex1.dto.CreatePollRequest;
import com.example.ex1.model.Poll;
import com.example.ex1.repo.OLD.OLDPollManager;
import com.example.ex1.repo.ex6.PollManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;


@CrossOrigin
@RestController
public class PollController {
    private final PollManager manager;

    public PollController(PollManager manager) {
        this.manager = manager;
    }

    @PostMapping("/poll/create")
    public Poll createPoll(@RequestBody CreatePollRequest r) {
        return manager.createPoll(r.userID, r.question, r.publishedAt, r.validUntil, r.options);
    }

    @GetMapping("/poll/all")
    public List<Poll> getPolls() {
        return manager.getPolls();
    }

    /*@GetMapping("/poll/{pollId}")
    public Poll getPoll(@PathVariable int pollId) {
        Poll p = manager.listPolls.get(pollId);
        if (p == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        }
        return p;
    }*/

    @GetMapping("/poll/{pollId}")
    public Map<String, Object> getPoll(@PathVariable int pollId) {
        var view = manager.pollView(pollId);
        if (view == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        return view;
    }

    @DeleteMapping("/poll/{pollId}")
    public void deletePoll(@PathVariable int pollId) {
        manager.deletePoll(pollId);
    }


    //ex6
    @PostMapping("/polls/{pollId}/vote")
    public ResponseEntity<?> vote(
            @PathVariable int pollId,
            @RequestParam int optionId,
            @RequestParam(required = false) Integer userId // may be null for anonymous
    ) {
        // Event-first write:
        manager.publishVoteEvent(pollId, optionId, userId);
        // The subscriber will apply state + invalidate cache.
        return ResponseEntity.accepted().body(Map.of("status", "published"));
    }

}
