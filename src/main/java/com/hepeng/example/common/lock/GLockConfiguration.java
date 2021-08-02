package com.hepeng.example.common.lock;

import com.hepeng.example.common.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.Redisson;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.env.Environment;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 分布式锁处理器
 * Created by 玄玉<https://jadyer.cn/> on 2018/6/5 10:00.
 */
@Aspect
//@Configuration
@ConditionalOnClass({RedissonClient.class})
@ConfigurationProperties(prefix="redis")
@Slf4j
public class GLockConfiguration implements EnvironmentAware {
    private List<String> nodes = new ArrayList<>();
    private String pattern;
    private int lockWatchdogTimeout;
    private int minIdle;
    private int maxTotal;
    private int connectionTimeout;
    private int database;
    private String password;
    private Environment environment;
    private static final String LOCK_PREFIX = GLock.class.getSimpleName() + ":";
    public static final List<RedissonClient> redissonClientList = new ArrayList<>();
    private final ExpressionParser parser = new SpelExpressionParser();
    private final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


    @PostConstruct
    public void initRedissonClientList(){
        log.info("RedissonClientList init beginning...");
        if(!redissonClientList.isEmpty()){
            log.info("RedissonClientList has inited, skip...");
            return;
        }
        if(StringUtils.equals(pattern, "SINGLE")){
            this.initSingleServerConfig();
        }else{
            this.initClusterServersConfig();
        }
        log.info("RedissonClientList init end...");
    }


    private void initSingleServerConfig(){
        for(String node : nodes){
            Config config = new Config();
            if(lockWatchdogTimeout > 0){
                config.setLockWatchdogTimeout(lockWatchdogTimeout);
            }
            SingleServerConfig singleConfig = config.useSingleServer().setAddress("redis://" + node);
            if(minIdle > 0){
                singleConfig.setConnectionMinimumIdleSize(minIdle);
            }
            if(maxTotal > 0){
                singleConfig.setConnectionPoolSize(maxTotal);
            }
            if(connectionTimeout > 0){
                singleConfig.setConnectTimeout(connectionTimeout);
            }
            if(database > 0){
                singleConfig.setDatabase(database);
            }
            if(StringUtils.isNotBlank(password)){
                singleConfig.setPassword(password);
            }
            redissonClientList.add(Redisson.create(config));
            log.info("RedissonClientList init on {}", node);
        }
    }


    private void initClusterServersConfig(){
        String[] nodeAddresses = new String[nodes.size()];
        for(int i=0,len=nodes.size(); i<len; i++){
            nodeAddresses[i] = "redis://" + nodes.get(i);
        }
        Config config = new Config();
        if(lockWatchdogTimeout > 0){
            config.setLockWatchdogTimeout(lockWatchdogTimeout);
        }
        ClusterServersConfig clusterConfig = config.useClusterServers().addNodeAddress(nodeAddresses);
        if(connectionTimeout > 0){
            clusterConfig.setConnectTimeout(connectionTimeout);
        }
        if(StringUtils.isNotBlank(password)){
            clusterConfig.setPassword(password);
        }
        redissonClientList.add(Redisson.create(config));
        log.info("RedissonClientList init on {}", (Object)nodeAddresses);
    }


    @Around("@annotation(com.hepeng.example.common.lock.GLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        GLock glock = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(GLock.class);
        String key = LOCK_PREFIX + (StringUtils.isBlank(glock.appname())?"":this.getPropertyFromEnv(glock.appname())+":") + this.parseSpringEL(glock, joinPoint);
        RLock[] rLocks = new RLock[redissonClientList.size()];
        for(int i=0; i<redissonClientList.size(); i++){
            rLocks[i] = redissonClientList.get(i).getLock(key);
        }
        RedissonRedLock redLock = null;
        try{
            try {
                //new RedissonRedLock(rLocks)可能发生异常：比如应用正在启动中，就来调用这里
                //Caused by: java.lang.IllegalArgumentException: Lock objects are not defined
                redLock = new RedissonRedLock(rLocks);
                if(!redLock.tryLock(glock.waitTime(), glock.leaseTime(), glock.unit())){
                    log.warn("资源[{}]加锁-->失败", key);
                    this.lockFallback(key, glock.fallbackMethod(), joinPoint);
                    return null;
                }
            } catch (Throwable t) {
                log.error("资源[{}]加锁-->失败：{}", key, CommonUtils.extractStackTraceCausedBy(t), t);
                this.lockFallback(key, glock.fallbackMethod(), joinPoint);
                return null;
            }
            log.debug("资源[{}]加锁-->成功", key);
            return joinPoint.proceed();
        }finally{
            if(null != redLock){
                redLock.unlock();
            }
            log.debug("资源[{}]解锁-->完毕", key);
        }
    }


    private String getPropertyFromEnv(String prop){
        if(StringUtils.isBlank(prop)){
            return "";
        }
        if(prop.startsWith("${") && prop.endsWith("}")){
            prop = prop.substring(2, prop.length()-1);
            prop = this.environment.getProperty(prop);
        }
        return prop;
    }


    private String parseSpringEL(GLock glock, ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
        Object[] args = joinPoint.getArgs();
        String key = StringUtils.isNotBlank(glock.key()) ? glock.key() : glock.value();
        if(StringUtils.isBlank(key)){
            key = joinPoint.getTarget().getClass().getSimpleName() + "." + method.getName();
        }
        if(!key.contains("#") && !key.contains("'")){
            return key;
        }
        String[] params = discoverer.getParameterNames(method);
        if(ObjectUtils.isEmpty(params)){
            return key;
        }
        EvaluationContext context = new StandardEvaluationContext();
        for(int i=0; i<params.length; i++){
            context.setVariable(params[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context, String.class);
    }


    /**
     * 加锁失败时的回调
     */
    private void lockFallback(String key, String fallbackMethod, ProceedingJoinPoint joinPoint){
        if(StringUtils.isBlank(fallbackMethod)){
            log.debug("资源[{}]加锁-->失败，未配置回调方法名，故不回调", key);
            return;
        }
        try {
            log.debug("资源[{}]加锁-->失败，回调开始...", key);
            Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
            Object[] args = joinPoint.getArgs();
            joinPoint.getTarget().getClass().getMethod(fallbackMethod, method.getParameterTypes()).invoke(joinPoint.getTarget(), args);
            log.debug("资源[{}]加锁-->失败，回调结束...", key);
        } catch (Throwable t) {
            log.error("资源[{}]加锁-->失败，回调失败，堆栈轨迹如下", key, t);
        }
    }


    public List<String> getNodes() {
        return nodes;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public int getLockWatchdogTimeout() {
        return lockWatchdogTimeout;
    }

    public void setLockWatchdogTimeout(int lockWatchdogTimeout) {
        this.lockWatchdogTimeout = lockWatchdogTimeout;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}