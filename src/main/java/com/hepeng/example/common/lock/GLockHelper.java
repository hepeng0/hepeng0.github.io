package com.hepeng.example.common.lock;

import com.hepeng.example.common.utils.CommonUtils;
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
 *     if(GLockHelper.lock(GLockConfiguration.redissonClientList, "资源key", "资源应用名称")){
 *         //业务处理-->start...
 *         HTTPUtil.post("the url", "the params");
 *         //业务处理-->end.....
 *     }
 * } finally {
 *     GLockHelper.unlock();
 * }
 * ---------------------------------------------------------------------------------------------------------------
 */
@Slf4j
public class GLockHelper {
    private static final String LOCK_PREFIX = GLockHelper.class.getSimpleName() + ":";
    private static final ThreadLocal<String> keyMap = new ThreadLocal<>();
    private static final ThreadLocal<RedissonRedLock> redLockMap = new ThreadLocal<>();

    public static boolean lock(List<RedissonClient> redissonClientList, String key){
        return lock(redissonClientList, key, null, -1, TimeUnit.SECONDS);
    }


    public static boolean lock(List<RedissonClient> redissonClientList, String key, String appName){
        return lock(redissonClientList, key, appName, -1, TimeUnit.SECONDS);
    }


    public static boolean lock(List<RedissonClient> redissonClientList, String key, String appName, long waitTime, TimeUnit unit){
        key = LOCK_PREFIX + (StringUtils.isBlank(appName)?"":appName+":") + key;
        RedissonRedLock redLock = redLockMap.get();
        if(null != redLock){
            log.error("资源[{}]加锁-->失败：上一次未解锁", key);
            return false;
        }
        //加锁
        RLock[] rLocks = new RLock[redissonClientList.size()];
        for(int i=0; i<redissonClientList.size(); i++){
            rLocks[i] = redissonClientList.get(i).getLock(key);
        }
        try {
            //new RedissonRedLock(rLocks)可能发生异常：比如应用正在启动中，就来调用这里
            //Caused by: java.lang.IllegalArgumentException: Lock objects are not defined
            redLock = new RedissonRedLock(rLocks);
            if(!redLock.tryLock(waitTime, unit)){
                log.error("资源[{}]加锁-->失败", key);
                return false;
            }
        } catch (Throwable t) {
            log.error("资源[{}]加锁-->失败：{}", key, CommonUtils.extractStackTraceCausedBy(t), t);
            return false;
        }
        keyMap.set(key);
        redLockMap.set(redLock);
        log.info("资源[{}]加锁-->成功", key);
        return true;
    }


    public static void unlock(){
        RedissonRedLock redLock = redLockMap.get();
        if(null != redLock){
            redLock.unlock();
            log.info("资源[{}]解锁-->完毕", keyMap.get());
        }
        keyMap.remove();
        redLockMap.remove();
    }
}