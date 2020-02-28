package funkye.icu.redis.cache.starter.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 陈健斌
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCache {
    /**
     * -前缀
     */
    String key() default "redisCache";

    /**
     * -缓存单位毫米,默认60秒
     */
    int timeoutMills() default 60000;

    /**
     * -默认0代表查询 ,如果设置为1,remove就会起作用
     */
    int type() default 0;

    /**
     * 带上要删除key的前缀,比如此时插入了新数据 会导致缓存脏读, 所以再插入时删除对应的缓存列表 比如执行updateUser等操作 那么此时应该填入findUser/pageuser等. 未填写时会删除库内所有缓存避免脏读
     * 
     * @return
     */
    String[] remove() default {"redisCache"};
}
