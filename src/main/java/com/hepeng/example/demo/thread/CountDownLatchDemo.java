package com.hepeng.example.demo.thread;

import java.util.concurrent.CountDownLatch;

/**
 * description CountDownLatchDemo
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/5/26 10:57
 * @since 1.0
 */
public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {

        //定义：允许一个或多个线程等待直到在其他线程中执行的一组操作完成的同步辅助。
        //用途：1.一个CountDownLatch为一个计数的CountDownLatch用作一个简单的开/关锁存器，或者门：
        //      所有线程调用await在门口等待，直到被调用countDown()的线程打开。
        //     2.一个CountDownLatch初始化N可以用来做一个线程等待，直到N个线程完成某项操作，或某些动作已经完成N次

        CountDownLatch countDownLatch = new CountDownLatch(20);
        for (int i=1 ;i<=20;i++){
//             new Thread(()->{
            countDownLatch.countDown();
            System.out.println(Thread.currentThread().getName()+">="+countDownLatch.getCount());
//            },String.valueOf(i)).start();

        }
        // 特性：它不要求调用countDown线程等待计数到达零之前继续，
        // 它只是阻止任何线程通过await ，直到所有线程可以通过。
        System.out.println("我可以在Await方法之前执行");

        countDownLatch.await();

        System.out.println("我为什么在最后执行呢");
    }
}
