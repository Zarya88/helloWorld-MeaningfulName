package com.example.ex1.repo.OLD;
import com.example.ex1.model.Poll;
import com.example.ex1.model.User;
import com.example.ex1.model.Vote;
import com.example.ex1.model.VoteOption;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OLDPollManager {
    public int userCounter;
    public HashMap<Integer, User> listUsers  = new HashMap<>();
    public int pollCounter;
    public HashMap<Integer, Poll> listPolls  = new HashMap<>();
    public int voteCounter;
    public HashMap<Integer, Vote> listVotes  = new HashMap<>();
    public int voteOptionCounter;
    public HashMap<Integer, VoteOption> listVoteOptions  = new HashMap<>();

    public OLDPollManager() {
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
    public Poll createPoll(int userId, String question, Instant publishedAt, Instant validUntil, List<String> options) {
        ArrayList<Integer> voteOptions = new ArrayList<>();
        Poll poll = new Poll(pollCounter, userId, question, publishedAt, validUntil, voteOptions);
        listPolls.put(pollCounter, poll);


        //listVoteOptions
        //create VoteOptions - you could use a for loop but IDs work
        for (int i = 0; i < options.size(); i++) {
            listVoteOptions.put(voteOptionCounter, new VoteOption(voteOptionCounter, options.get(i), i, new ArrayList<Integer>()));
            voteOptions.add(voteOptionCounter);
            this.voteOptionCounter++;
        }
        this.pollCounter++;
        return poll;
    }

    public List<Poll> getPolls() {
        return listPolls.values().stream().toList();
    }

    public void deletePoll(int pollId) {
        listPolls.remove(pollId);
    }

    //Votes
    public Vote createVote(int userId, int voteOptID, Instant publishedAt) {
        Vote vote = new Vote(userId, this.voteCounter, voteOptID, publishedAt);
        listVotes.put(this.voteCounter, vote);
        this.voteCounter++;
        return vote;
    }

    public Vote replaceVote(int userId, int voteOptID, Instant publishedAt) {
        for (Vote v : listVotes.values()){
            if (userId == v.userId) {
                Vote vote = new Vote(userId, v.voteID, voteOptID, publishedAt);
                listVotes.replace(v.voteID, vote);
                return vote;
            }
        }
        return null;
    }

    public List<Vote> showAllVotes(){
        return listVotes.values().stream().toList();
    }


    public Map<String, Object> pollView(int pollId) {
        var p = listPolls.get(pollId);
        if (p == null) return null;

        var options = new ArrayList<Map<String, Object>>();
        for (Integer optId : p.options) {
            var vo = listVoteOptions.get(optId);
            if (vo == null) continue;

            long votes = listVotes.values().stream()
                    .filter(v -> v.voteOptionID == optId)   // adapt field name if needed
                    .count();

            var o = new HashMap<String, Object>();
            o.put("id",    vo.voteOptionID);
            o.put("text",  vo.caption);
            o.put("votes", votes);
            options.add(o);
        }

        var out = new HashMap<String, Object>();
        out.put("id",       p.pollID);
        out.put("question", p.question);
        out.put("options",  options);
        return out;
    }

}
