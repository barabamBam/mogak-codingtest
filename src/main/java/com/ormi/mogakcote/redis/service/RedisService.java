package com.ormi.mogakcote.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService<V> {
    private final RedisTemplate<String, V> redisTemplate;

    public void set(String key, V value, long timeoutSeconds) {
        redisTemplate.opsForValue().set(key, value, timeoutSeconds, TimeUnit.SECONDS);
    }

    public V getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Boolean contains(String key) {
        return redisTemplate.hasKey(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}