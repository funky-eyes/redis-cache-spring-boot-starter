package icu.funkye.redis.cache.starter.service.impl;

import java.time.Duration;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import icu.funkye.redis.cache.starter.service.IRedisCacheService;

/**
 * -redis服务实现
 *
 * @author chenjianbin
 * @version 1.0.0
 */
@DependsOn({"redisCacheTemplate"})
@Service("redisCacheService")
public class RedisCacheServiceImpl<K, V> implements IRedisCacheService<K, V> {

    @Autowired
    private RedisTemplate<K, V> redisCacheTemplate;

    @Override
    public void set(K key, V value, Duration timeout) {
        redisCacheTemplate.opsForValue().set(key, value, timeout);
    }

    @Override
    public Boolean delete(K key) {
        return redisCacheTemplate.delete(key);
    }

    @Override
    public V get(K key) {
        return redisCacheTemplate.opsForValue().get(key);
    }

    @Override
    public Boolean setIfAbsent(K key, V value, Duration timeout) {
        return redisCacheTemplate.opsForValue().setIfAbsent(key, value, timeout);
    }

    @Override
    public Set<K> keys(K key) {
        // TODO Auto-generated method stub
        return redisCacheTemplate.keys(key);
    }

    @Override
    public Long delete(Set<K> key) {
        // TODO Auto-generated method stub
        return redisCacheTemplate.delete(key);
    }

}
