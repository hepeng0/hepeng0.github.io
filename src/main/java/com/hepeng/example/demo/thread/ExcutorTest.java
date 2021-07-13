package com.hepeng.example.demo.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * description ExcutorTest
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/5/13 11:23
 * @since 1.0
 */
public class ExcutorTest {
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    private String message = "hello word";

    public void threadTest() {
        executorService.execute(new Thread(() -> {
            System.out.println(Thread.currentThread().getId() + message);
        }));
    }

    public void runnableTest() {
        executorService.execute((Runnable) () -> {
            System.out.println(Thread.currentThread().getId() + message);
        });
    }

    public void callableTest() {
        executorService.submit((Callable<Long>) () -> {
            System.out.println(Thread.currentThread().getId() + message);
            return Thread.currentThread().getId();
        });
    }
}
