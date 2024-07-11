package com.itender.threadpool.completablefuture;

import com.itender.threadpool.config.ExecutorConfig;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author yuanhewei
 * @date 2024/7/11 16:51
 * @description
 */
public class CompletableFutureIntro {
    public static void main(String[] args) {
        Executor executor = new ExecutorConfig().completableExecutor();
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "completableFuture", executor);
        System.out.println(future.join());
    }
}
