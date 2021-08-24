package com.hepeng.example.common.lock;

import com.hepeng.example.common.utils.CommonUtils;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * --------------------------------------------------------------------------------------------------------------
 * try {
 *  if(GLockHelper.lock(GLockConfiguration.redissonClientList, "资源key", "资源应用名称")){
 *      //业务处理-->start...
 *      HTTPUtil.post("the url", "the params");
 *      //业务处理-->end.....
 *   }
 * } finally {
 *   GLockHelper.unlock();
 * } ---------------------------------------------------------------------------------------------------------------
 */
@Slf4j
public class GLockHelper {

    private static final String LOCK_PREFIX = GLockHelper.class.getSimpleName() + ":";
    /**
     * key: value - lockKey: RedissonRedLock
     */
    private static final Map<String, Pair<RedissonRedLock, Pair<Thread, AtomicInteger>>> redLockMap = new ConcurrentHashMap<>();

    /**
     * 加锁--加锁失败不等待直接返回false
     *
     * @param key key
     * @return true/false
     */
    public static boolean lock(String key) {
        return lock(key, -1, TimeUnit.SECONDS);
    }

    /**
     * 加锁-不可重入锁
     *
     * @param key      key
     * @param waitTime 等待时长
     * @param unit     时间单位
     * @return true/false
     */
    public static boolean lock(String key, long waitTime, TimeUnit unit) {
        return lock(key, waitTime, unit, false);
    }

    /**
     * 加锁
     *
     * @param key       锁Key
     * @param waitTime  等待时间
     * @param unit      时间单位
     * @param reentrant 是否可重入
     * @return true/false
     */
    public static boolean lock(String key, long waitTime, TimeUnit unit, boolean reentrant) {
        List<RedissonClient> redissonClientList = GLockConfiguration.redissonClientList;
        key = LOCK_PREFIX + key;
        Pair<RedissonRedLock, Pair<Thread, AtomicInteger>> redissonRedLockPairPair = redLockMap
            .get(key);
        if (reentrant && Objects.nonNull(redissonRedLockPairPair)) {
            if (redissonRedLockPairPair.getValue().getKey().equals(Thread.currentThread())) {
                int times = redissonRedLockPairPair.getValue().getValue().incrementAndGet();
                log.info("资源[{}]加锁成功-->当前重入层级[{}]", key, times);
                return true;
            }
        }
        RedissonRedLock redLock;
        //加锁
        RLock[] rLocks = new RLock[redissonClientList.size()];
        for (int i = 0; i < redissonClientList.size(); i++) {
            rLocks[i] = redissonClientList.get(i).getLock(key);
        }
        try {
            //new RedissonRedLock(rLocks)可能发生异常：比如应用正在启动中，就来调用这里
            //Caused by: java.lang.IllegalArgumentException: Lock objects are not defined
            redLock = new RedissonRedLock(rLocks);
            if (!redLock.tryLock(waitTime, unit)) {
                log.error("资源[{}]加锁-->失败", key);
                return false;
            }
        } catch (Throwable t) {
            log.error("资源[{}]加锁-->失败：", key, t);
            return false;
        }
        redLockMap.put(key,
            new Pair<>(redLock, new Pair<>(Thread.currentThread(), new AtomicInteger(1))));
        log.info("资源[{}]加锁-->成功", key);
        return true;
    }

    /**
     * 解锁
     *
     * @param key key
     */
    public static void unlock(String key) {
        key = LOCK_PREFIX + key;
        Pair<RedissonRedLock, Pair<Thread, AtomicInteger>> redissonRedLockPairPair = redLockMap
            .get(key);
        if (Objects.isNull(redissonRedLockPairPair)) {
            log.warn("资源[{}]解锁-->失败, 锁不存在", key);
            return;
        }
        Pair<Thread, AtomicInteger> value = redissonRedLockPairPair.getValue();
        if (value.getValue().get() <= 1) {
            RedissonRedLock redLock = redissonRedLockPairPair.getKey();
            if (null != redLock) {
                redLock.unlock();
                log.info("资源[{}]解锁-->完毕", key);
            }
            redLockMap.remove(key);
        } else {
            int times = value.getValue().getAndDecrement();
            log.info("资源[{}]释放一次-->剩余重入层级[{}]", key, times);
        }

    }
}