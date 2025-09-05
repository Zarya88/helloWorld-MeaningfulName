package com.example.ex1.repo;
import com.example.ex1.model.Poll;
import com.example.ex1.model.User;
import com.example.ex1.model.VoteOption;

import java.time.Instant;
import java.util.HashMap;

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
    public void addNewUser(String username, String email) {
        listUsers.put(this.userCounter, new User(this.userCounter, username, email));
        this.userCounter++;
    }

    public void deleteUser(int userId) {
        listUsers.remove(userId);
    }

    public HashMap<Integer, User> showAllUsers(){
        return listUsers;
    }

    //POLLS
    public void createPoll(int userId, String question, Instant publishedAt, Instant validUntil) {
        Poll poll = new Poll(pollCounter, userId, question, publishedAt, validUntil);
        listPolls.put(userId, poll);
        this.pollCounter++;


    }

    public void deletePoll(int pollId) {
        listPolls.remove(pollId);
    }

    //Votes
    public void createVote(userId, ) {

    }

    public void replaceVote() {
    }

}
