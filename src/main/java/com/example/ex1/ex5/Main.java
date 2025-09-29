package com.example.ex1.ex5;

import redis.clients.jedis.UnifiedJedis;

public class Main {
    public static void main(String[] args) {
        // Connect to Redis running locally on port 6379
        try (UnifiedJedis jedis = new UnifiedJedis("redis://localhost:6379")) {

            // exCode SET + GET
            jedis.set("user", "bob");
            System.out.println("User from Redis: " + jedis.get("user"));

            // exCode: key with expiry
            jedis.expire("user", 5);
            System.out.println("TTL: " + jedis.ttl("user"));
        }
    }
}
