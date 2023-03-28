package com.itender.threadpool.service;

/**
 * @author itender
 * @date 2023/1/29 10:35
 * @desc
 */
public interface LogOutputService {
    /**
     * 多线程插入
     *
     * @return
     */
    int multiThreadInsert();

    /**
     * 单线程插入
     *
     * @return
     */
    int singleThreadInsert();
}
