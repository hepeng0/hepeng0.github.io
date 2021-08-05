package com.hepeng.example.demo.memoryleak;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Peng.He6
 * @date 2021/8/4 11:27
 * @DESCRIPTION
 */
public class MemoryLeak {
    public String test() {
        return test();
    }
    public static void main(String[] args) {
        MemoryLeak memoryLeak = new MemoryLeak();
        System.out.println(memoryLeak.test());
    }
}
