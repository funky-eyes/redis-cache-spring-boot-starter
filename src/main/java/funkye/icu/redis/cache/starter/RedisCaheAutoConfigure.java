package funkye.icu.redis.cache.starter;

import java.time.Duration;

import javax.annotation.PostConstruct;

import funkye.icu.redis.cache.starter.config.JedisCacheProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.Jedis;

@ComponentScan(basePackages = {"funkye.icu.redis.cache.starter.config", "funkye.icu.redis.cache.starter.service",
    "funkye.icu.redis.cache.starter.aspect"})
@EnableConfigurationProperties({JedisCacheProperties.class})
@ConditionalOnClass(Jedis.class)
@Configuration
public class RedisCaheAutoConfigure {

    @Autowired
    private JedisCacheProperties prop;
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCaheAutoConfigure.class);

    @PostConstruct
    public void load() {
        LOGGER.info("redis缓存始化中........................");
    }

    @Bean
    @Primary
    public JedisConnectionFactory jedisCacheConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration
            .setHostName(null == prop.getHost() || prop.getHost().length() <= 0 ? "127.0.0.1" : prop.getHost());
        redisStandaloneConfiguration.setPort(prop.getPort() <= 0 ? 6379 : prop.getPort());
        redisStandaloneConfiguration.setDatabase(prop.getDataBase() <= 0 ? 0 : prop.getDataBase());
        if (prop.getPassword() != null && prop.getPassword().length() > 0) {
            redisStandaloneConfiguration.setPassword(RedisPassword.of(prop.getPassword()));
        }
        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration =
            JedisClientConfiguration.builder();
        jedisClientConfiguration.connectTimeout(Duration.ofMillis(prop.getTimeOut()));// connection timeout
        JedisConnectionFactory factory =
            new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration.build());
        LOGGER.info("redis初始化完成:{}........................", prop);
        return factory;
    }

    @DependsOn({"jedisCacheConnectionFactory"})
    @Bean("redisCacheTemplate")
    public RedisTemplate<String, Object> redisCacheTemplate(
        @Qualifier("jedisCacheConnectionFactory") JedisConnectionFactory jedisCacheConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisCacheConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(redisTemplate.getKeySerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
