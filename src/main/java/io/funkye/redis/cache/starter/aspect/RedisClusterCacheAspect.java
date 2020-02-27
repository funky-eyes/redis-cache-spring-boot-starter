package io.funkye.redis.cache.starter.aspect;

import java.time.Duration;
import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.funkye.redis.cache.starter.config.annotation.RedisCache;
import io.funkye.redis.cache.starter.service.IRedisCacheService;

/**
 * -动态拦截分布式锁
 *
 * @author chenjianbin
 * @version 1.0.0
 */
@DependsOn({"redisCacheService"})
@Aspect
@Component
public class RedisClusterCacheAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClusterCacheAspect.class);

    @Autowired
    private IRedisCacheService<String, Object> redisCacheService;

    @Pointcut("@annotation(io.funkye.redis.cache.starter.config.annotation.RedisCache)")
    public void annotationPoinCut() {}

    @Around(value = "annotationPoinCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        RedisCache annotation = signature.getMethod().getAnnotation(RedisCache.class);
        String key = annotation.key();
        Object o = null;
        if (annotation.type() == 0) {
            key = key + joinPoint.getTarget().getClass().getName() + signature.getName() + getNameAndValue(joinPoint);
            o = redisCacheService.get(key);
            if (o != null) {
                LOGGER.info("########## 命中缓存:{} ##########", key);
                return o;
            } else {
                LOGGER.info("########## 缓存不存在:{} ##########", key);
            }
        }
        try {
            o = joinPoint.proceed();
        } catch (Throwable e) {
            LOGGER.error("出现异常:{}", e.getMessage());
            throw e;
        }
        if (annotation.type() == 1) {
            String[] removeKey = annotation.remove();
            Long keys = 0L;
            if (removeKey == null || removeKey.length <= 0) {
                if (removeKey[0].length() <= 0) {
                    keys = redisCacheService.delete(redisCacheService.keys(annotation.key() + "*"));
                    if (keys > 0) {
                        LOGGER.info("########## 清除缓存成功:{} ##########", keys);
                    }
                }
            } else {
                for (int i = 0; i < removeKey.length; i++) {
                    Set<String> deletes = redisCacheService.keys(annotation.key() + "*");
                    keys += redisCacheService.delete(deletes);
                }
                LOGGER.info("########## 清除缓存成功:{} ##########", keys);
            }
        } else {
            LOGGER.info("########## 添加缓存成功:{} ##########", key);
            redisCacheService.setIfAbsent(key, o, Duration.ofMillis(annotation.timeoutMills()));
        }
        return o;
    }

    /**
     * 获取key的拼接字符串
     * 
     * @param joinPoint
     * @return
     */
    static String getNameAndValue(ProceedingJoinPoint joinPoint) {
        StringBuffer strings = new StringBuffer();
        Object[] paramValues = joinPoint.getArgs();
        String[] paramNames = ((CodeSignature)joinPoint.getSignature()).getParameterNames();

        for (int i = 0; i < paramNames.length; i++) {
            strings.append(paramNames[i] + paramValues[i]);
        }

        return strings.toString();
    }

}
