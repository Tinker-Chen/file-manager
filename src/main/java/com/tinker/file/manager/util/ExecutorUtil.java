package com.tinker.file.manager.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ExecutorUtil
 *
 * @author Tinker Chen
 * @date 2017/10/30.
 */
public class ExecutorUtil {

    public static ExecutorService getInstance() {
        ExecutorService executorService = new ThreadPoolExecutor(5, 10,
                60L,TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), new ThreadPoolExecutor.AbortPolicy());
        return executorService;
    }
}
