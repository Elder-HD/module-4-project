package org.example.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.Getter;


public class RedisUtil {
    @Getter
    private static final RedisClient redisClient;

    static {
        redisClient = RedisClient.create(RedisURI.create("localhost", 6379));
        redisClient.connect();
        System.out.println("\nConnected to Redis\n");
    }
}
