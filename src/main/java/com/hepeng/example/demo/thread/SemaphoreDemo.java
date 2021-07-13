package com.hepeng.example.demo.thread;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * description SemaphoreDemo
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/5/26 11:19
 * @since 1.0
 */
public class SemaphoreDemo {
    public static void main(String[] args) {
        // 限流：停车位为3，车位满之后，等待车走，再进一个。
        // 多个资源的互斥使用
        Semaphore semaphore = new Semaphore(3);
        for (int i = 1; i <= 6; i++) {
            new Thread(() -> {
                try {
                    //先占一个位
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + "抢到了车位");
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println(Thread.currentThread().getName() + "离开了车位");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // 释放一个位置
                    semaphore.release();
                }
            }, String.valueOf(i)).start();
        }
    }
}
