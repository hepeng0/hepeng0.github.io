package com.hepeng.example.common.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * @author: Peng.He6
 * @description:
 * @date:created in 2021/8/24 16:41
 * @modificed by:
 */
class CommonUtilsTest {

    @Test
    void extractStackTrace() {
        System.out.println(CommonUtils.extractStackTraceCausedBy(new RuntimeException(
            new IllegalArgumentException(
                new RuntimeException(new IllegalStateException("测试测试"))))));
    }
}