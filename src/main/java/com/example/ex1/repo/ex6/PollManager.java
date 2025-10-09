package com.example.ex1.repo.ex6;

import com.example.ex1.model.Poll;
import com.example.ex1.model.User;
import com.example.ex1.model.Vote;
import com.example.ex1.model.VoteOption;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    // --- ex5: cache + pub/sub ---
    private final JedisPool pool;
    private final ObjectMapper om = new ObjectMapper();
    private final int TTL_SECONDS = 120;

    private ExecutorService subscriberExecutor;
    private volatile boolean running = true;

    //ex6
    private Jedis subscriberConn; //connection for psubscribe
    private JedisPubSub subscriber;

    public PollManager(JedisPool pool) {
        this.pool = pool;
        this.userCounter = 1;
        this.pollCounter = 1;
        this.voteCounter = 1;
        this.voteOptionCounter = 1;
    }

    // -------- cache keys / channels ----------
    private static String cacheKey(int pollId) { return "poll:" + pollId + ":view"; }
    private static String channel(int pollId)   { return "poll:" + pollId; }          // topic per poll
    private static String channelPattern()      { return "poll:*"; }                   // subscribe to all polls


    @PostConstruct
    public void startSubscriber() {
        subscriber = new JedisPubSub() {
            @Override
            public void onPMessage(String pattern, String ch, String msg) {
                // handle message...
            }
        };

        subscriberExecutor = Executors.newSingleThreadExecutor();
        subscriberExecutor.submit(() -> {
            try {
                subscriberConn = pool.getResource();
                subscriberConn.psubscribe(subscriber, channelPattern());
            } catch (Exception e) {
                if (running) e.printStackTrace();
            }
        });
    }

    @PreDestroy
    public void stopSubscriber() {
        running = false;
        try {
            if (subscriber != null) {
                try { subscriber.punsubscribe(); } catch (Exception ignored) {}
            }
            if (subscriberConn != null) {
                subscriberConn.close();
            }
        } catch (Exception ignored) {}
        if (subscriberExecutor != null) subscriberExecutor.shutdownNow();
    }



    // ============== (unchanged) ==============

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
        // no explicit "topic creation" needed for Redis; first publish will create the channel implicitly
        this.pollCounter++;
        return poll;
    }

    public List<Poll> getPolls() { return listPolls.values().stream().toList(); }
    public void deletePoll(int pollId) { listPolls.remove(pollId); invalidateCache(pollId); }

    // NOTE: leave these methods for direct (non-event) writes if you need them internally,
    // but prefer publishing an event and letting the subscriber apply state.
    public Vote createVote(int userId, int voteOptID, Instant publishedAt) {
        Vote vote = new Vote(userId, this.voteCounter, voteOptID, publishedAt);
        listVotes.put(this.voteCounter, vote);
        this.voteCounter++;

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

    // ---------------------- cache wrappers ----------------------

    public Map<String, Object> pollViewCached(int pollId) {
        String key = cacheKey(pollId);
        try (Jedis jedis = pool.getResource()) {
            String json = jedis.get(key);
            if (json != null) {
                return om.readValue(json, new TypeReference<Map<String,Object>>(){});
            }
            Map<String, Object> view = pollView(pollId);
            if (view != null) {
                jedis.setex(key, TTL_SECONDS, om.writeValueAsString(view));
            }
            return view;
        } catch (Exception e) {
            return pollView(pollId);
        }
    }

    public void invalidateCache(int pollId) {
        try (Jedis jedis = pool.getResource()) {
            jedis.del(cacheKey(pollId));
        } catch (Exception ignored) {}
    }

    private Integer findPollIdByVoteOption(int voteOptId) {
        for (var entry : listPolls.entrySet()) {
            if (entry.getValue().options.contains(voteOptId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    // ===================== Pub/Sub API =====================

    /** Publish a vote event to the poll’s channel. userId may be null for anonymous. */
    public void publishVoteEvent(int pollId, int optionId, Integer userId) {
        Map<String, Object> evt = new HashMap<>();
        evt.put("type", "vote");
        evt.put("pollId", pollId);
        evt.put("optionId", optionId);
        evt.put("userId", userId); // can be null
        evt.put("ts", Instant.now().toString());

        try (Jedis j = pool.getResource()) {
            j.publish(channel(pollId), om.writeValueAsString(evt));
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish vote event", e);
        }
    }

    /** Apply vote event locally (invoked by subscriber). */
    private void applyVoteEvent(Integer pollId, Integer optionId, Integer maybeUserId) {
        if (pollId == null || optionId == null) return;
        // Validate that option belongs to poll
        Poll p = listPolls.get(pollId);
        if (p == null || !p.options.contains(optionId)) return;

        int userId = (maybeUserId == null ? 0 : maybeUserId); // 0 = anonymous
        // If user already voted, replace; else create
        Optional<Vote> existing = listVotes.values().stream().filter(v -> v.userId == userId).findFirst();
        if (existing.isPresent()) {
            Vote v = existing.get();
            Vote updated = new Vote(userId, v.voteID, optionId, Instant.now());
            listVotes.put(v.voteID, updated);
        } else {
            Vote v = new Vote(userId, this.voteCounter, optionId, Instant.now());
            listVotes.put(this.voteCounter, v);
            this.voteCounter++;
        }
    }
}
