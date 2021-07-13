package com.hepeng.example.demo.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * description RedisLockUtil
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/5/7 16:50
 * @since 1.0
 */
@Component
public final class RedisLockUtil {
    private static final int defaultExpire = 60;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private RedisLockUtil() {
        //
    }

    /**
     * 加锁
     * @param key    redis key
     * @param expire 过期时间，单位秒
     * @return true:加锁成功，false，加锁失败
     */
    public boolean lock(String key, int expire) {

        long value = System.currentTimeMillis() + expire;
        Boolean status = redisTemplate.opsForValue().setIfAbsent(key, String.valueOf(value));

        if (status) {
            return true;
        }
        long oldExpireTime = Long.parseLong(redisTemplate.opsForValue().get(key));
        if (oldExpireTime < System.currentTimeMillis()) {
            //超时
            long newExpireTime = System.currentTimeMillis() + expire;
            long currentExpireTime = Long.parseLong(Objects.requireNonNull(redisTemplate.opsForValue()
                    .getAndSet(key, String.valueOf(newExpireTime))));
            if (currentExpireTime == oldExpireTime) {
                return true;
            }
        }
        return false;
    }

    public boolean lock(String key) {
        return lock(key, defaultExpire);
    }


    public void unLock(String key) {
        RedisTemplate redisTemplate = new RedisTemplate();
        long oldExpireTime = Long.parseLong(Objects.requireNonNull(redisTemplate.opsForValue().get(key)).toString());
        if (oldExpireTime > System.currentTimeMillis()) {
            redisTemplate.delete(key);
        }
    }
}
