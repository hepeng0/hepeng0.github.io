package com.hepeng.example.demo.thread;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * description CyclicBarrierDemo
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/5/26 11:16
 * @since 1.0
 */
public class CyclicBarrierDemo {
    public static void main(String[] args) {

        //适用需等待多个操作完成，再执行下一步
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7,()->{
            System.out.println("舍利子集齐成功，如来重生");
        });

        for (int i=1;i<=7;i++){
            int finalI = i;
            new Thread(()->{
                System.out.println(Thread.currentThread().getName()+"：收集了"+ finalI +"颗");
                try {
                    //等待，舍利子集齐，一起向下执行
                    cyclicBarrier.await();

                    System.out.println("无天必须在如来重生之后，再死");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
