package com.ratection.gammamonitor.service;

import com.ratection.gammamonitor.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RedisService {

    @Autowired
    private RedisClient redisClient;

    public static final String PREFIX_TOKEN = "gamma-monitor-token:info:";

    public RedisService(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public void addToken(String token) {
        redisClient.set(PREFIX_TOKEN + token, null);
    }

    public Boolean exist(String token) {
        return redisClient.exists(PREFIX_TOKEN + token);
    }

    public void clearToken(String token) {
        redisClient.remove(PREFIX_TOKEN + token);
    }

}
