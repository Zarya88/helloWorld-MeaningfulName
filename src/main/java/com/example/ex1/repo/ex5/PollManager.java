package com.example.ex1.repo.ex5;

import com.example.ex1.model.Poll;
import com.example.ex1.model.User;
import com.example.ex1.model.Vote;
import com.example.ex1.model.VoteOption;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.Jedis;

import java.time.Instant;
import java.util.*;

@Service
public class PollManager {

    public int userCounter;
    public HashMap<Integer, User> listUsers  = new HashMap<>();
    public int pollCounter;
    public HashMap<Integer, Poll> listPolls  = new HashMap<>();
    public int voteCounter;
    public HashMap<Integer, Vote> listVotes  = new HashMap<>();
    public int voteOptionCounter;
    public HashMap<Integer, VoteOption> listVoteOptions  = new HashMap<>();

    //ex5
    private final JedisPool pool;
    private final ObjectMapper om = new ObjectMapper();
    private final int TTL_SECONDS = 120; // choose what you like

    // Spring injects JedisPool
    public PollManager(JedisPool pool) {
        this.pool = pool;
        this.userCounter = 1;
        this.pollCounter = 1;
        this.voteCounter = 1;
        this.voteOptionCounter = 1;
    }

    private static String cacheKey(int pollId) {
        return "poll:" + pollId + ":view";        // e.g. poll:42:view
    }

    // ---------------------- (unchanged) ----------------------

    public User createUser(String username, String email) {
        User user = new User(this.userCounter, username, email);
        listUsers.put(this.userCounter, user);
        this.userCounter++;
        return user;
    }

    public void deleteUser(int userId) { listUsers.remove(userId); }
    public HashMap<Integer, User> showAllUsers(){ return listUsers; }
    public User getUser(int userId) { return listUsers.get(userId); }

    public Poll createPoll(int userId, String question, Instant publishedAt, Instant validUntil, List<String> options) {
        ArrayList<Integer> voteOptions = new ArrayList<>();
        Poll poll = new Poll(pollCounter, userId, question, publishedAt, validUntil, voteOptions);
        listPolls.put(pollCounter, poll);

        for (int i = 0; i < options.size(); i++) {
            listVoteOptions.put(voteOptionCounter, new VoteOption(voteOptionCounter, options.get(i), i, new ArrayList<Integer>()));
            voteOptions.add(voteOptionCounter);
            this.voteOptionCounter++;
        }
        this.pollCounter++;
        return poll;
    }

    public List<Poll> getPolls() { return listPolls.values().stream().toList(); }
    public void deletePoll(int pollId) { listPolls.remove(pollId); invalidateCache(pollId); }

    public Vote createVote(int userId, int voteOptID, Instant publishedAt) {
        Vote vote = new Vote(userId, this.voteCounter, voteOptID, publishedAt);
        listVotes.put(this.voteCounter, vote);
        this.voteCounter++;

        // Invalidate the affected poll in cache
        Integer pollId = findPollIdByVoteOption(voteOptID);
        if (pollId != null) invalidateCache(pollId);

        return vote;
    }

    public Vote replaceVote(int userId, int voteOptID, Instant publishedAt) {
        for (Vote v : listVotes.values()){
            if (userId == v.userId) {
                Vote vote = new Vote(userId, v.voteID, voteOptID, publishedAt);
                listVotes.replace(v.voteID, vote);

                Integer pollId = findPollIdByVoteOption(voteOptID);
                if (pollId != null) invalidateCache(pollId);

                return vote;
            }
        }
        return null;
    }

    public List<Vote> showAllVotes(){ return listVotes.values().stream().toList(); }

    // Build the Map that your controllers return (existing logic)
    public Map<String, Object> pollView(int pollId) {
        var p = listPolls.get(pollId);
        if (p == null) return null;

        var options = new ArrayList<Map<String, Object>>();
        for (Integer optId : p.options) {
            var vo = listVoteOptions.get(optId);
            if (vo == null) continue;

            long votes = listVotes.values().stream()
                    .filter(v -> v.voteOptionID == optId)
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

    // ---------------------- ex5: caching wrappers ----------------------

    /** Read-through cache: returns pollView from Redis if present, else computes & caches it. */
    public Map<String, Object> pollViewCached(int pollId) {
        String key = cacheKey(pollId);
        try (Jedis jedis = pool.getResource()) {
            String json = jedis.get(key);
            if (json != null) {
                return om.readValue(json, new TypeReference<Map<String,Object>>(){});
            }
            // Miss → compute & cache
            Map<String, Object> view = pollView(pollId);
            if (view != null) {
                jedis.setex(key, TTL_SECONDS, om.writeValueAsString(view));
            }
            return view;
        } catch (Exception e) {
            // If Redis fails, fall back to computing directly
            return pollView(pollId);
        }
    }

    // invalidate cache after mutations (new vote, delete poll, etc.)
    public void invalidateCache(int pollId) {
        try (Jedis jedis = pool.getResource()) {
            jedis.del(cacheKey(pollId));
        } catch (Exception ignored) {}
    }

    //find which poll contains a given voteOption id
    private Integer findPollIdByVoteOption(int voteOptId) {
        for (var entry : listPolls.entrySet()) {
            if (entry.getValue().options.contains(voteOptId)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
