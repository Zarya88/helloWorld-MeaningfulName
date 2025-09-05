package com.example.ex1.repo;
import com.example.ex1.model.Poll;
import com.example.ex1.model.User;
import com.example.ex1.model.Vote;
import com.example.ex1.model.VoteOption;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PollManager {
    public int userCounter;
    public HashMap<Integer, User> listUsers  = new HashMap<>();
    public int pollCounter;
    public HashMap<Integer, Poll> listPolls  = new HashMap<>();
    public int voteCounter;
    public HashMap<Integer, User> listVotes  = new HashMap<>();
    public int voteOptionCounter;
    public HashMap<Integer, Poll> listVoteOptions  = new HashMap<>();

    public PollManager() {
        this.userCounter = 1;
        this.pollCounter = 1;
        this.voteCounter = 1;
        this.voteOptionCounter = 1;
    }

    //USER STUFF
    public User createUser(String username, String email) {
        User user = new User(this.userCounter, username, email);
        listUsers.put(this.userCounter, user);
        this.userCounter++;
        return user;
    }

    public void deleteUser(int userId) {
        listUsers.remove(userId);
    }

    public HashMap<Integer, User> showAllUsers(){
        return listUsers;
    }

    public User getUser(int userId) {
        return listUsers.get(userId);
    }

    //POLLS
    public Poll createPoll(int userId, String question, Instant publishedAt, Instant validUntil, ArrayList<String> options) {
        Poll poll = new Poll(pollCounter, userId, question, publishedAt, validUntil);
        listPolls.put(userId, poll);

        ArrayList<VoteOption> voteOptions = new ArrayList<>();

        //listVoteOptions
        //create VoteOptions - you could use a for loop but IDs work
        for (int i = 0; i < options.size(); i++) {
            voteOptions.add(new VoteOption(voteOptionCounter, options.get(i), i, new ArrayList<Integer>()));
        }
        this.pollCounter++;
        this.voteOptionCounter++;
        return poll;
    }

    public List<Poll> getPolls() {
        return listPolls.values().stream().toList();
    }

    //TODO: public getPoll()

    public void deletePoll(int pollId) {
        listPolls.remove(pollId);
    }

    //Votes
    public void createVote(int userId, int voteOptID, Instant publishedAt) {
        Vote vote = new Vote(userId, this.voteCounter, voteOptID, publishedAt);
        this.voteCounter++;
    }

    public void replaceVote() {
        //TODO: replace Vote
    }

}
