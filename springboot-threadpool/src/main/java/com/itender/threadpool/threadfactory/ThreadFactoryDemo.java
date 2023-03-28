package com.itender.threadpool.threadfactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @author itender
 * @date 2022/6/29 17:50
 * @desc 线程工厂
 */
public class ThreadFactoryDemo {

    /**
     * 定义线程工厂
     */
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("线程-demo-%d").build();

    /**
     * 自定义线程池
     */
    private static final ThreadPoolExecutor THREAD_POOL = new ThreadPoolExecutor(2, 4, 60, TimeUnit.SECONDS, new
            ArrayBlockingQueue<>(10), THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) {
        for (int i = 1; i <= 15; i++) {
            Callable<Boolean> task = createTask(i);
            THREAD_POOL.submit(task);
            System.out.println("after task:" + i + " submitted, current active count: "
                    + THREAD_POOL.getActiveCount() + ", size of queue: " + THREAD_POOL.getQueue().size());
        }
        THREAD_POOL.shutdown();
    }

    /**
     * 创建任务
     *
     * @param i
     * @return
     */
    private static Callable<Boolean> createTask(int i) {
        return () -> {
            TimeUnit.SECONDS.sleep(10);
            System.out.println("thread: " + Thread.currentThread().getName() + " execute task: " + i);
            return true;
        };
    }

}
