# redis-cache-spring-boot-starter

#### 介绍
redis实现二级缓存

#### 软件架构

通过maven方式引入本插件依赖:

```java
<dependency>
  <groupId>icu.funkye</groupId>
  <artifactId>redis-cache-spring-boot-starter</artifactId>
  <version>0.1</version>
</dependency>
```

项目需引入如下依赖方可使用:

```java
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-autoconfigure</artifactId>
            <version>2.1.8.RELEASE</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
            <version>2.9.1</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
            <version>2.1.8.RELEASE</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
            <version>2.1.8.RELEASE</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
```

#### 使用说明

1. ```java
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
   ```

   

2. 将以上注解加入需要使用二级缓存的地方

3. ``` yaml
   redis.cache.server.host=127.0.0.1 #默认
   redis.cache.server.port=6379 #默认
   redis.cache.server.password= #默认""
   redis.cache.server.database= #默认0
   ```