package com.itender.threadpool.completablefuture;

import com.itender.threadpool.config.ExecutorConfig;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author yuanhewei
 * @date 2024/7/11 16:51
 * @description
 */
public class CompletableFutureIntro {
    public static void main(String[] args) {
        // // 自定义线程池
        Executor executor = new ExecutorConfig().customerExecutor();
        // supplyAsync-自定义线程池
        CompletableFuture<String> supplyAsyncCustomer = CompletableFuture.supplyAsync(() -> {
            return "supplyAsync-customer：" + Thread.currentThread().getName();
        }, executor);
        // supplyAsync-默认线程池
        CompletableFuture<String> supplyAsyncDefault = CompletableFuture.supplyAsync(() -> {
            return "supplyAsync-default：" + Thread.currentThread().getName();
        });
        // 获取结果  join()和get()方法都是用来获取CompletableFuture异步之后的返回值。
        // join()方法抛出的是uncheck异常（即未经检查的异常),不会强制开发者抛出。
        // get()方法抛出的是经过检查的异常，ExecutionException, InterruptedException 需要用户手动处理（抛出或者 try catch）
        System.out.println(supplyAsyncCustomer.join());
        System.out.println(supplyAsyncDefault.join());
        try {
            String result1 = supplyAsyncCustomer.get();
            String result2 = supplyAsyncDefault.get();
            System.out.println(result1);
            System.out.println(result2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        // runAsync-自定义线程池
        CompletableFuture.runAsync(() -> System.out.println("runAsync-customer：" + Thread.currentThread().getName()), executor);
        // runAsync-默认线程池
        CompletableFuture.runAsync(() -> System.out.println("runAsync-default：" + Thread.currentThread().getName()));

        // thenApply
        CompletableFuture<Integer> thenApply = CompletableFuture
                .supplyAsync(() -> {
                    int result = 999;
                    System.out.println("第一次运算的result: " + result);
                    return result;
                })
                .thenApply(result -> {
                    result = result * 2;
                    System.out.println("第二次运算的result: " + result);
                    return result;
                });
        System.out.println("最终结果：" + thenApply.join());

        CompletableFuture<Integer> thenApplyAsyncDefault = CompletableFuture
                .supplyAsync(() -> {
                    int result = 999;
                    System.out.println("第一次运算,thread= " + Thread.currentThread().getName() + " result= " + result);
                    return result;
                })
                .thenApplyAsync(result -> {
                    result = result * 2;
                    System.out.println("第二次运算,thread= " + Thread.currentThread().getName() + " result= " + result);
                    return result;
                });
        System.out.println("最终结果：,thread= " + Thread.currentThread().getName() + " result= " + thenApplyAsyncDefault.join());

        // thenApplyAsync自定义线程池
        CompletableFuture<Integer> thenApplyAsyncCustomer = CompletableFuture
                .supplyAsync(() -> {
                    int result = 999;
                    System.out.println("第一次运算,thread= " + Thread.currentThread().getName() + " result= " + result);
                    return result;
                })
                .thenApplyAsync(result -> {
                    result = result * 2;
                    System.out.println("第二次运算,thread= " + Thread.currentThread().getName() + " result= " + result);
                    return result;
                }, executor);
        System.out.println("最终结果：,thread= " + Thread.currentThread().getName() + " result= " + thenApplyAsyncCustomer.join());


        CompletableFuture.completedFuture("hello!")
                .thenApply(s -> s + "world!")
                .thenApply(s -> s + "nice!")
                .thenAccept(System.out::println);// hello!world!nice!

        CompletableFuture.completedFuture("hello!")
                .thenApply(s -> s + "world!")
                .thenApply(s -> s + "nice!")
                .thenRun(() -> System.out.println("hello!"));// hello!
        // thenAccept
        CompletableFuture<Void> thenAccept = CompletableFuture
                .supplyAsync(() -> {
                    int result = 100;
                    System.out.println("第一次运算：" + result);
                    return result;
                }).thenAccept(result ->
                        System.out.println("第二次运算：" + result * 5));
        System.out.println("thenAccept = " + thenAccept.join());
        // thenRun
        CompletableFuture<Void> thenRun = CompletableFuture
                .supplyAsync(() -> {
                    int result = 100;
                    System.out.println("第一次运算：" + result);
                    return result;
                }).thenRun(() ->
                        System.out.println("第二次运算：不接收不处理第一次的运算结果！"));
        System.out.println("thenRun = " + thenRun.join());


        // whenComplete 无异常
        CompletableFuture<Integer> whenComplete = CompletableFuture
                .supplyAsync(() -> {
                    int result = 100;
                    System.out.println("第一次运算：" + result);
                    return result;
                }).whenComplete((result, ex) ->
                        System.out.println("第二次运算：" + result * 5));
        System.out.println("whenComplete = " + whenComplete.join());

        // whenComplete 异常
        CompletableFuture<Integer> whenCompleteException = CompletableFuture
                .supplyAsync(() -> {
                    int result = 100;
                    System.out.println("第一次运算：" + result);
                    int i = 1 / 0;
                    return result;
                }).whenComplete((result, ex) -> {
                            System.out.println("处理异常为：" + ex);
                            System.out.println("第二次运算：" + result * 5);
                        }
                );
        System.out.println("whenCompleteException = " + whenCompleteException.join());

        // handle 异常
        // handle
        CompletableFuture<Integer> handle = CompletableFuture
                .supplyAsync(() -> {
                    int result = 100;
                    System.out.println("第一次运算：" + result);
                    int i = 1 / 0;
                    return result;
                }).handle((result, ex) -> {
                            System.out.println("处理异常为：" + ex.getMessage());
                            System.out.println("第二次运算：" + result * 5);
                            return result * 5;
                        }
                );
        System.out.println("handle = " + handle.join());

        CompletableFuture<String> future
                = CompletableFuture.supplyAsync(() -> {
            if (true) {
                throw new RuntimeException("Computation error!");
            }
            return "hello!";
        }).handle((res, ex) -> {
            // res 代表返回的结果
            // ex 的类型为 Throwable ，代表抛出的异常
            return res != null ? res : "world!";
        });
        System.out.println("future = " + future.join());

        // exceptionally 异常
        CompletableFuture<Integer> exceptionally = CompletableFuture
                .supplyAsync(() -> {
                    int result = 100;
                    System.out.println("第一次运算：" + result);
                    int i = 1 / 0;
                    return result;
                }).exceptionally(ex -> {
                            System.out.println("处理异常为：" + ex.getMessage());
                            return 100;
                        }
                );
        System.out.println("exceptionally = " + exceptionally.join());

        // thenCombine
        CompletableFuture<String> helloAsync = CompletableFuture.supplyAsync(() -> {
            System.out.println("hello 执行线程：" + Thread.currentThread().getName());
            return "hello";
        });
        CompletableFuture<String> worldAsync = CompletableFuture.supplyAsync(() -> {
            System.out.println("world 执行线程：" + Thread.currentThread().getName());
            return "world";
        });
        CompletableFuture<String> thenCombine = worldAsync.thenCombine(helloAsync, (hello, world) -> {
            System.out.println("result 执行线程：" + Thread.currentThread().getName());
            return (hello + "," + world).toUpperCase();
        });
        System.out.println("获取结果 执行线程：" + Thread.currentThread().getName());
        System.out.println("thenCombine 两个异步任务合并结果:" + thenCombine.join());

        // thenCompose
        CompletableFuture<String> thenCompose = CompletableFuture.supplyAsync(() -> {
            System.out.println("hello 执行线程：" + Thread.currentThread().getName());
            return "hello";
        }).thenCompose((hello -> {
            System.out.println("thenCompose 执行线程：" + Thread.currentThread().getName());
            return CompletableFuture.supplyAsync((hello + "world")::toUpperCase);
        }));
        System.out.println("获取结果 执行线程：" + Thread.currentThread().getName());
        System.out.println("thenCompose 两个异步任务流水线执行结果:" + thenCompose.join());

        long startTime = System.currentTimeMillis();
        System.out.println("startTime = " + startTime);
        CompletableFuture<String> orderFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("orderFuture-查询订单信息完成！");
            return "orderFuture-查询订单信息完成！";
        });
        CompletableFuture<String> orderDetailsFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("orderDetailsFuture-查询订单明细信息完成！");
            return "orderDetailsFuture-查询订单明细信息完成！";
        });
        CompletableFuture<String> productFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("productFuture-查询商品信息完成！");
            return "productFuture-查询商品信息完成！";
        });
        CompletableFuture<String> skuFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("skuFuture-查询sku信息完成！");
            return "skuFuture-查询sku信息完成!";
        });
        // 组合
        CompletableFuture<Void> resultFuture = CompletableFuture.allOf(orderFuture, orderDetailsFuture, productFuture, skuFuture);
        resultFuture.join();
        long endTime = System.currentTimeMillis();
        System.out.println("endTime = " + endTime);
        System.out.println("任务总耗时：" + (endTime - startTime));
        // 组合结果
        System.out.println("最终结果为：" + orderFuture.join() + "\n" + orderDetailsFuture.join() + "\n" + productFuture.join() + "\n" + skuFuture.join());
    }
}
