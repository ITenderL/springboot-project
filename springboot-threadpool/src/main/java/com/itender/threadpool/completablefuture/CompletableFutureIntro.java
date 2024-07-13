package com.itender.threadpool.completablefuture;

import com.itender.threadpool.config.ExecutorConfig;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

/**
 * @author yuanhewei
 * @date 2024/7/11 16:51
 * @description
 */
public class CompletableFutureIntro {
    public static void main(String[] args) {
        // // 自定义线程池
        Executor executor = new ExecutorConfig().customerExecutor();
        // // supplyAsync-自定义线程池
        // CompletableFuture<String> supplyAsyncCustomer = CompletableFuture.supplyAsync(() -> {
        //     return "supplyAsync-customer：" + Thread.currentThread().getName();
        // }, executor);
        // // supplyAsync-默认线程池
        // CompletableFuture<String> supplyAsyncDefault = CompletableFuture.supplyAsync(() -> {
        //     return "supplyAsync-default：" + Thread.currentThread().getName();
        // });
        // // 获取结果  join()和get()方法都是用来获取CompletableFuture异步之后的返回值。
        // // join()方法抛出的是uncheck异常（即未经检查的异常),不会强制开发者抛出。
        // // get()方法抛出的是经过检查的异常，ExecutionException, InterruptedException 需要用户手动处理（抛出或者 try catch）
        // System.out.println(supplyAsyncCustomer.join());
        // System.out.println(supplyAsyncDefault.join());
        // try {
        //     String result1 = supplyAsyncCustomer.get();
        //     String result2 = supplyAsyncDefault.get();
        //     System.out.println(result1);
        //     System.out.println(result2);
        // } catch (InterruptedException e) {
        //     throw new RuntimeException(e);
        // } catch (ExecutionException e) {
        //     throw new RuntimeException(e);
        // }
        // // runAsync-自定义线程池
        // CompletableFuture.runAsync(() -> System.out.println("runAsync-customer：" + Thread.currentThread().getName()), executor);
        // // runAsync-默认线程池
        // CompletableFuture.runAsync(() -> System.out.println("runAsync-default：" + Thread.currentThread().getName()));

        // thenApply
        // CompletableFuture<Integer> thenApply = CompletableFuture
        //         .supplyAsync(() -> {
        //             int result = 999;
        //             System.out.println("第一次运算的result: " + result);
        //             return result;
        //         })
        //         .thenApply(result -> {
        //             result = result * 2;
        //             System.out.println("第二次运算的result: " + result);
        //             return result;
        //         });
        // System.out.println("最终结果：" + thenApply.join());

        // CompletableFuture<Integer> thenApplyAsyncDefault = CompletableFuture
        //         .supplyAsync(() -> {
        //             int result = 999;
        //             System.out.println("第一次运算,thread= " + Thread.currentThread().getName() + " result= " + result);
        //             return result;
        //         })
        //         .thenApplyAsync(result -> {
        //             result = result * 2;
        //             System.out.println("第二次运算,thread= " + Thread.currentThread().getName() + " result= " + result);
        //             return result;
        //         });
        // System.out.println("最终结果：,thread= " + Thread.currentThread().getName() + " result= " + thenApplyAsyncDefault.join());
        //
        // // thenApplyAsync自定义线程池
        // CompletableFuture<Integer> thenApplyAsyncCustomer = CompletableFuture
        //         .supplyAsync(() -> {
        //             int result = 999;
        //             System.out.println("第一次运算,thread= " + Thread.currentThread().getName() + " result= " + result);
        //             return result;
        //         })
        //         .thenApplyAsync(result -> {
        //             result = result * 2;
        //             System.out.println("第二次运算,thread= " + Thread.currentThread().getName() + " result= " + result);
        //             return result;
        //         }, executor);
        // System.out.println("最终结果：,thread= " + Thread.currentThread().getName() + " result= " + thenApplyAsyncCustomer.join());


        CompletableFuture<Void> future = CompletableFuture
                .supplyAsync(() -> {
                    int number = new Random().nextInt(10);
                    System.out.println("第一次运算：" + number);
                    return number;
                }).thenAccept(number ->
                        System.out.println("第二次运算：" + number * 5));

    }
}
