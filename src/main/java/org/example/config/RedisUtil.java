package org.example.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import lombok.Getter;


public class RedisUtil {
    @Getter
    private static final RedisClient lettuceClient;

    static {
        lettuceClient = RedisClient.create(RedisURI.create("localhost", 6379));
        lettuceClient.connect();
    }
}
