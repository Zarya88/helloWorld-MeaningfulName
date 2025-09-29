package com.example.ex1.repo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
public class RedisConfig {
    @Bean
    public JedisPool jedisPool() {
        // adjust host/port if needed
        return new JedisPool("localhost", 6379);
    }
}
