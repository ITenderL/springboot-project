# `CompletableFuture` 介绍与实战

## 一、前言

​	日常工作中，大多数情况下我们的接口的执行逻辑都是串行化的，串行化的逻辑也基本能满足我们绝大部分的场景。但是，在一些情况下我们的代码可能会存在一些比较耗时的操作，串行的逻辑就有可能造成代码的阻塞，影响用户的体验。这种情况下就需要我们对一些复杂的场景，耗时的操作做一些异步并行的操作，来提升代码的执行效率，从而提升用户的体验。

​	在我们项目中就有这样一种场景，在一个报表的统计中，我们需要同时计算相关指标的当期值，上期值，同比，环比等。这就需要我们根据不同的时间段查询出相应的数据，然后在进行同比，环比等计算，每次请求接口都涉及2 - 3查询。随着业务的增长，数据量的增加接口的响应时间也是越来越慢。时间区间跨度选择越大，接口响应越慢。针对这种情况，我们采用了多线程并行查询不同时间段的数据的操作，接口的效率也是有了肉眼可见的提升。

​	还有在复杂的查询中可能需要同时需要查询多个对象的信息返回给页面。A,B,C,使用串行查询的耗时就是A + B  + C的耗时，使用并行查询的话耗时就是A,B,C中耗时最久的一个查询。

- 串行查询

![串行查询流程图](assets\串行查询流程图.png)

- 并行查询

![并行查询流程图](assets\并行查询流程图.png)

## 二、`CompletableFuture` 介绍

### 2.1  概述

`CompletableFuture`类是Java8引入的，提供了灵活的异步处理的能力，同时`CompletableFuture`支持对任务的编排的能力。

![image-20240712172356690](assets\image-20240712172356690.png)



``` java
public class CompletableFuture<T> implements Future<T>, CompletionStage<T> {
}
```

`CompletableFuture`实现了`Future`和`CompletionStage`接口。

这里值得说一点的是在使用`CompletableFuture`进行异步任务处理的时候，如果不指定线程池的话，默认会使用`ForkJoinPool.commonPool()`作为线程池。

``` java

    public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) {
        return asyncSupplyStage(asyncPool, supplier);
    }

    private static final boolean useCommonPool =
        (ForkJoinPool.getCommonPoolParallelism() > 1);
    /**
     * Default executor -- ForkJoinPool.commonPool() unless it cannot
     * support parallelism.
     */
    private static final Executor asyncPool = useCommonPool ?
        ForkJoinPool.commonPool() : new ThreadPerTaskExecutor();
```

**异步执行的，默认线程池是`ForkJoinPool.commonPool()`，但为了业务之间互不影响，且便于定位问题，强烈推荐使用自定义线程池**。

## 三、常用功能

环境准备

``` java
@Bean(name = "customerExecutor")
public Executor customerExecutor() {
    log.info("start customerExecutor");
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    // 核心线程数
    executor.setCorePoolSize(4);
    // 最大线程数
    executor.setMaxPoolSize(6);
    // 任务队列大小
    executor.setQueueCapacity(20);
    // 线程名称前缀
    // executor.setThreadNamePrefix("completableFuture-async-");
    // 配置线程工厂
    executor.setThreadFactory(namedThreadFactory);
    // 拒绝策略
    // rejection-policy：当pool已经达到max size的时候，如何处理新任务
    // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    // 初始化
    executor.initialize();
    return executor;
}
```

### 3.1  异步操作`supplyAsync` 和 `runAsync`

#### 3.1.1 功能介绍

`CompletableFuture`提供了四个静态方法来执行异步的任务。

``` java
    /**
     * 有返回值，使用默认线程池
     */
    public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) {
        return asyncSupplyStage(asyncPool, supplier);
    }
    /**
     * 有返回值，使用自定义线程池（推荐使用）
     */
    public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier,Executor executor) {
        return asyncSupplyStage(screenExecutor(executor), supplier);
    }
    /**
     * 无返回值，使用默认线程池
     */
    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        return asyncRunStage(asyncPool, runnable);
    }
    /**
     * 无返回值，使用自定义线程池（推荐使用）
     */
    public static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor) {
        return asyncRunStage(screenExecutor(executor), runnable);
    }
```

- `runAsync()` 以`Runnable`函数式接口类型为参数，没有返回结果，

- `supplyAsync()` 以`Supplier`函数式接口类型为参数，返回结果类型为U；Supplier接口的 get()是有返回值的(会阻塞)

- 使用没有指定线程池`Executor`的方法时，内部使用`ForkJoinPool.commonPool()` 作为它的线程池执行异步代码。如果指定线程池，则使用指定的线程池运行。

- 默认情况下`CompletableFuture`会使用公共的`ForkJoinPool`线程池，这个线程池默认创建的线程数是 CPU 的核数（也可以通过 JVM `option:-Djava.util.concurrent.ForkJoinPool.common.parallelism` 来设置`ForkJoinPool`线程池的线程数）。
- 如果所有`CompletableFuture`共享一个线程池，那么一旦有任务执行一些很慢的 I/O 操作，就会导致线程池中所有线程都阻塞在 I/O 操作上，从而造成线程饥饿，进而影响整个系统的性能。所以，强烈建议你要根据不同的业务类型创建不同的线程池，以避免互相干扰

#### 3.1.2 功能演示

##### 3.1.2.1 `supplyAsync()`

- 代码演示

``` java
public static void main(String[] args) {
    // 自定义线程池
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
}
```

- 输出结果

``` txt
supplyAsync-customer：customer-executor-pool-0
supplyAsync-default：ForkJoinPool.commonPool-worker-9
supplyAsync-customer：customer-executor-pool-0
supplyAsync-default：ForkJoinPool.commonPool-worker-9
```

**注意：**

1. `join()` 和 `get()` 方法都可以获取`CompletableFuture`异步执行结果的返回值**(阻塞调用者线程，等待异步线程返回结果)。**。
2. `get()` 方法抛出可检查异常`ExecutionException`, `InterruptedException`，需要抛出`throw`或者捕获`try/catch`异常。
3. `join()` 抛出不可检查异常。

##### 3.1.2.2 `runAsync()`代码演示

- 代码演示

``` java
public static void main(String[] args) {
        // 自定义线程池
        Executor executor = new ExecutorConfig().customerExecutor();
        // runAsync-自定义线程池
        CompletableFuture.runAsync(() -> System.out.println("runAsync-customer：" + Thread.currentThread().getName()), executor);
        // runAsync-默认线程池
        CompletableFuture.runAsync(() -> System.out.println("runAsync-default：" + Thread.currentThread().getName()));
    }
```

- 输出结果

``` txt
runAsync-customer：customer-executor-pool-1
runAsync-default：ForkJoinPool.commonPool-worker-9
```

### 3.2 处理异步操作的结果

当我们采用异步操作进行任务处理时，拿到异步任务的结果后还可以对结果进行进一步的处理和操作，常用的方法有下面几个：

- `thenApply()`
- `thenAccept()`
- `thenRun()`
- `whenComplete()`

将上一段任务的执行结果作为下一阶段任务的入参参与重新计算，产生新的结果。

#### 3.2.1 `thenApply()`

##### 3.2.1.1 功能介绍

`thenApply`接收一个函数作为参数，使用该函数处理上一个`CompletableFuture`调用的结果，并返回一个具有处理结果的`Future`对象。

``` java
// 使用上一个任务的结果，沿用上一个任务的线程池
public <U> CompletableFuture<U> thenApply(Function<? super T,? extends U> fn);
// 使用默认线程池
public <U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> fn);
// 使用自定义线程池
public <U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> fn, Executor executor) {
    return uniApplyStage(screenExecutor(executor), fn);
}
```



##### 3.2.1.2 功能演示

- 代码演示

``` java
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

// thenApplyAsync 默认线程池
CompletableFuture<Integer> thenApplyAsyncDefault = CompletableFuture
        .supplyAsync(() -> {
            int result = 999;
            System.out.println("第一次运算,thread= "+ Thread.currentThread().getName()+" result= " + result);
            return result;
        })
        .thenApplyAsync(result -> {
            result = result * 2;
            System.out.println("第二次运算,thread= "+ Thread.currentThread().getName()+" result= " + result);
            return result;
        });
System.out.println("最终结果：,thread= "+ Thread.currentThread().getName()+" result= " + thenApplyAsyncDefault.join());

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
```

- 输出结果

``` txt
第一次运算的result: 999
第二次运算的result: 1998
最终结果：1998

第一次运算,thread= ForkJoinPool.commonPool-worker-9 result= 999
第二次运算,thread= ForkJoinPool.commonPool-worker-9 result= 1998
最终结果：,thread= main result= 1998

第一次运算,thread= ForkJoinPool.commonPool-worker-9 result= 999
第二次运算,thread= customer-executor-pool-0 result= 1998
最终结果：,thread= main result= 1998
```

#### 3.2.2 `thenAccept()`和`thenRun()`

**如果你不需要从回调函数中获取返回结果，可以使用 `thenAccept()` 或者 `thenRun()`。**

**这两个方法的区别在于 `thenRun()` 不能访问异步计算的结果。**

#####  3.2.2.1 功能介绍

- `thenAccept()`

``` java
public CompletableFuture<Void> thenAccept(Consumer<? super T> action) {
    return uniAcceptStage(null, action);
}
public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action) {
    return uniAcceptStage(defaultExecutor(), action);
}
public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action,
                                               Executor executor) {
    return uniAcceptStage(screenExecutor(executor), action);
}
```

- `thenRun()`

``` java
public CompletableFuture<Void> thenRun(Runnable action) {
    return uniRunStage(null, action);
}
public CompletableFuture<Void> thenRunAsync(Runnable action) {
    return uniRunStage(defaultExecutor(), action);
}
public CompletableFuture<Void> thenRunAsync(Runnable action,
                                            Executor executor) {
    return uniRunStage(screenExecutor(executor), action);
}
```

##### 3.2.2.2 功能演示

- 代码演示

``` java
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
               System.out.println("第二次运算：不接收不处理第一次的运算结果！" ));
System.out.println("thenRun = " + thenRun.join());

```

- 输出结果

``` java
第一次运算：100
第二次运算：500
thenAccept = null
    
第一次运算：100
第二次运算：不接收不处理第一次的运算结果！
thenRun = null
```

#### 3.2.3 `whenComplete()`

##### 3.2.3.1 功能介绍

当`CompletableFuture`的计算结果完成，或者抛出异常的时候，我们可以执行特定的 Action。主要是下面的方法：

```java
public CompletableFuture<T> whenComplete(
    BiConsumer<? super T, ? super Throwable> action) {
    return uniWhenCompleteStage(null, action);
}
// 使用默认线程池
public CompletableFuture<T> whenCompleteAsync(
    BiConsumer<? super T, ? super Throwable> action) {
    return uniWhenCompleteStage(defaultExecutor(), action);
}
// 使用自定义线程池(推荐)
public CompletableFuture<T> whenCompleteAsync(
    BiConsumer<? super T, ? super Throwable> action, Executor executor) {
    return uniWhenCompleteStage(screenExecutor(executor), action);
}

```

##### 3.2.3.2 功能演示

- 代码演示

``` java
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
```

- 输出结果

``` java
第一次运算：100
第二次运算：500
whenComplete = 100
    
第一次运算：100
处理异常为：java.util.concurrent.CompletionException: java.lang.ArithmeticException: / by zero
Exception in thread "main" java.util.concurrent.CompletionException: java.lang.ArithmeticException: / by zero
	at java.util.concurrent.CompletableFuture.encodeThrowable(CompletableFuture.java:273)
	at java.util.concurrent.CompletableFuture.completeThrowable(CompletableFuture.java:280)
	at java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1592)
	at java.util.concurrent.CompletableFuture$AsyncSupply.exec(CompletableFuture.java:1582)
	at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
	at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1056)
	at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)
	at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)
Caused by: java.lang.ArithmeticException: / by zero
	at com.itender.threadpool.completablefuture.CompletableFutureIntro.lambda$main$2(CompletableFutureIntro.java:131)
	at java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1590)
	... 5 more
```

### 3.3 异常处理

当我们采用异步操作进行任务处理时，中间如果有任务发生了异常，我们可以通过下面几种方式进行处理：

- `handle()`

`handle` 是执行任务完成时对结果的处理， `handle` 方法和 `thenApply` 方法处理方式大致一样，不同的是 handle 是在任务完成后再执行且Handle可以根据可以根据任务是否有异常来进行做相应的后续处理操作。

- `exceptionally()`

`exceptionally也可以用来处理异常，发生异常后，执行`exceptionally方法。

- `whenComplete()`

`whenComplete()`方法前面已经演示过了，这里就不做过多的赘述了。

##### 3.3.1 功能介绍

- `handle()`

``` java
public <U> CompletableFuture<U> handle(
    BiFunction<? super T, Throwable, ? extends U> fn) {
    return uniHandleStage(null, fn);
}

public <U> CompletableFuture<U> handleAsync(
    BiFunction<? super T, Throwable, ? extends U> fn) {
    return uniHandleStage(defaultExecutor(), fn);
}

public <U> CompletableFuture<U> handleAsync(
    BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
    return uniHandleStage(screenExecutor(executor), fn);
}

```

- `exceptionally()`

``` java
public CompletableFuture<T> exceptionally(
        Function<Throwable, ? extends T> fn) {
        return uniExceptionallyStage(fn);
    }
```

##### 3.3.2 功能演示

- 代码演示

``` java
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

// CompletableFuture<String> future
//         = CompletableFuture.supplyAsync(() -> {
//     if (true) {
//         throw new RuntimeException("Computation error!");
//     }
//     return "hello!";
// }).handle((res, ex) -> {
//     // res 代表返回的结果
//     // ex 的类型为 Throwable ，代表抛出的异常
//     return res != null ? res : "world!";
// });
// System.out.println("future = " + future.join());

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
```

- 输出结果

此处handle和exceptionally结果为分开输出的

``` java
第一次运算：100
处理异常为：java.lang.ArithmeticException: / by zero
Exception in thread "main" java.util.concurrent.CompletionException: java.lang.NullPointerException
	at java.util.concurrent.CompletableFuture.encodeThrowable(CompletableFuture.java:273)
	at java.util.concurrent.CompletableFuture.completeThrowable(CompletableFuture.java:280)
	at java.util.concurrent.CompletableFuture.uniHandle(CompletableFuture.java:824)
	at java.util.concurrent.CompletableFuture$UniHandle.tryFire(CompletableFuture.java:797)
	at java.util.concurrent.CompletableFuture.uniHandleStage(CompletableFuture.java:837)
	at java.util.concurrent.CompletableFuture.handle(CompletableFuture.java:2155)
	at com.itender.threadpool.completablefuture.CompletableFutureIntro.main(CompletableFutureIntro.java:147)
Caused by: java.lang.NullPointerException
	at com.itender.threadpool.completablefuture.CompletableFutureIntro.lambda$main$1(CompletableFutureIntro.java:149)
	at java.util.concurrent.CompletableFuture.uniHandle(CompletableFuture.java:822)
	... 4 more
    
    
第一次运算：100
处理异常为：java.lang.ArithmeticException: / by zero
exceptionally = 100
```

### 3.4 结果组合

`	`提供了可以组合任务的方法。

#### 3.4.1 功能介绍

- `thenCombine()` 

`thenCombine` 会在两个`CompletableFuture`任务都执行完成后，把两个任务的结果一块处理。

``` java
public <U,V> CompletionStage<V> thenCombine(CompletionStage<? extends U> other,BiFunction<? super T,? super U,? extends V> fn);

public <U,V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other,BiFunction<? super T,? super U,? extends V> fn);

public <U,V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other,BiFunction<? super T,? super U,? extends V> fn,Executor executor);

```

- `thenCompose()`

`thenCompose` 方法允许你对两个`CompletableFuture`任务进行流水线操作，当第一个异步任务操作完成时，会将其结果作为参数传递给第二个任务。

``` java
public <U> CompletableFuture<U> thenCompose(
    Function<? super T, ? extends CompletionStage<U>> fn) {
    return uniComposeStage(null, fn);
}

public <U> CompletableFuture<U> thenComposeAsync(
    Function<? super T, ? extends CompletionStage<U>> fn) {
    return uniComposeStage(defaultExecutor(), fn);
}

public <U> CompletableFuture<U> thenComposeAsync(
    Function<? super T, ? extends CompletionStage<U>> fn,
    Executor executor) {
    return uniComposeStage(screenExecutor(executor), fn);
}

```



#### 3.4.2 功能演示

- 代码演示 `thenCombine()`

``` java
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
```

- 输出结果

``` java
hello 执行线程：ForkJoinPool.commonPool-worker-1
world 执行线程：ForkJoinPool.commonPool-worker-1
result 执行线程：main
获取结果 执行线程：main
thenCombine 两个异步任务合并结果:WORLD,HELLO
```

- 代码演示 `thenCompose()`

``` java
        CompletableFuture<String> thenCompose = CompletableFuture.supplyAsync(() -> {
            System.out.println("hello 执行线程：" + Thread.currentThread().getName());
            return "hello";
        }).thenCompose((hello -> {
            System.out.println("thenCompose 执行线程：" + Thread.currentThread().getName());
            return CompletableFuture.supplyAsync((hello + "world")::toUpperCase);
        }));
        System.out.println("获取结果 执行线程：" + Thread.currentThread().getName());
        System.out.println("thenCompose 两个异步任务流水线执行结果:" + thenCompose.join());
```

- 输出结果

``` java
hello 执行线程：ForkJoinPool.commonPool-worker-1
thenCompose 执行线程：main
获取结果 执行线程：main
thenCompose 两个异步任务流水线执行结果:HELLOWORLD
```



### 3.5 并行运行多个 `CompletableFuture`

#### 3.5.1 功能介绍

`CompletableFuture` 的 `allOf()`这个静态方法来并行运行多个 `CompletableFuture`;

在实际的工作中，会有很多这样的场景，比如：查询订单明细，要订单信息，订单明细，商品，sku等。四个不相关的任务可以同时独立操作查询。

``` java
public static CompletableFuture<Object> anyOf(CompletableFuture<?>... cfs)
```

#### 3.5.2 功能演示

- 代码演示

``` java
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

```

- 输出结果

``` java
startTime = 1721030494355
skuFuture-查询sku信息完成！
productFuture-查询商品信息完成！
orderDetailsFuture-查询订单明细信息完成！
orderFuture-查询订单信息完成！
endTime = 1721030496369
任务总耗时：2014
最终结果为：orderFuture-查询订单信息完成！
orderDetailsFuture-查询订单明细信息完成！
productFuture-查询商品信息完成！
skuFuture-查询sku信息完成!

```

## 四、总结

本文主要讲了`CompletableFuture`的一些功能的用法和集中常用的API。`CompletableFuture`提供了灵活的异步任务处理的方式，使用起来也是比较的方便。这里要注意的是，**在使用`CompletableFuture`的时候一定要使用自定义的线程池。**如果要想深入的理解`CompletableFuture`，还要自己多用心的去学习。希望文章内容能对大家理解`CompletableFuture`有一定的帮助，有问题或好的提议也请大家指出来，大家一起学习，一起进步。

纸上得来终觉浅，绝知此事要躬行。

## 参考

- https://blog.csdn.net/sermonlizhi/article/details/123356877?spm=1001.2014.3001.5506
- https://blog.csdn.net/leilei1366615/article/details/119855928?spm=1001.2014.3001.5506
- https://tech.meituan.com/2022/05/12/principles-and-practices-of-completablefuture.html