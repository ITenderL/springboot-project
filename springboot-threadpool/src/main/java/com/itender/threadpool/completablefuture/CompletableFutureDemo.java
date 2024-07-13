package com.itender.threadpool.completablefuture;

import com.itender.threadpool.config.ExecutorConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author itender
 * @date 2022/7/18 10:42
 * @desc CompletableFuture
 */
@Slf4j
public class CompletableFutureDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 创建CompletableFuture对象
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        // 获取返回对象的值
        // String result = completableFuture.get();
        // log.info(result);
        completableFuture.complete("Future's result!");

        // 使用 runAsync() 运行异步计算
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                log.error("Exception:{}, msg:{}", e, e.getMessage());
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
            log.info("I'll run in a separate thread than the main thread.");
        });
        log.info("voidCompletableFuture result: {}", voidCompletableFuture.get());


        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
            return "Result of the supplyAsync!";
        });
        String result = stringCompletableFuture.get();
        log.info("有返回值的异步调用 supplyAsync result: {}", result);

        Executor executor = new ExecutorConfig().customerExecutor();
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
            return "Result of the asynchronous computation";
        }, executor);
        log.info("自定义线程池 - 有返回值的异步调用 supplyAsync result: {}", future.get());


        // Create a CompletableFuture
        CompletableFuture<String> whatsYourNameFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
            return "ITender";
        }).thenApply(name -> "Hello " + name);

        // Block and get the result of the future.
        System.out.println("CompletableFuture 结果拼接 " + whatsYourNameFuture.get());


        CompletableFuture<String> welcomeText = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
            return "Rajeev";
        }).thenApply(name ->
                "Hello " + name
        ).thenApply(greeting ->
                greeting + ", Welcome to the CalliCoder Blog"
        );
        System.out.println("CompletableFuture 多次结果拼接 " + welcomeText.get());
    }
}
