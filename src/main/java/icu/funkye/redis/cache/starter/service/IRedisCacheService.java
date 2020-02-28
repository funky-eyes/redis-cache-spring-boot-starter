package icu.funkye.redis.cache.starter.service;

import java.time.Duration;
import java.util.Set;

/**
 * redis分布式锁实现
 * 
 * @author funkye
 * @version 1.0.0
 */
public interface IRedisCacheService<K, V> {

    /**
     * -分布式锁实现,只有锁的key不存在才会返回true
     */
    public Boolean setIfAbsent(K key, V value, Duration timeout);

    void set(K key, V o, Duration timeout);

    Boolean delete(K key);

    Long delete(Set<K> key);

    V get(K key);

    Set<K> keys(K key);
}
