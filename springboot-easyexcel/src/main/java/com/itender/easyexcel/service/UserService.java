package com.itender.easyexcel.service;

import com.itender.easyexcel.pojo.User;

import javax.servlet.ServletOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * @author itender
 * @date 2023/1/30 17:43
 * @desc
 */
public interface UserService {

    /**
     * 导出文件
     *
     * @param outputStream
     */
    void exportUserInfo(ServletOutputStream outputStream);

    /**
     * 导入文件
     *
     * @param inputStream
     */
    void importUserInfo(InputStream inputStream);

    /**
     * 填充excel
     *
     * @param outputStream
     */
    void fill(ServletOutputStream outputStream);

    /**
     * 查询user集合
     *
     * @return
     */
    List<User> userList();
}
