package com.itender.easyexcel.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.WriteDirectionEnum;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.itender.easyexcel.ExcelDataListener;
import com.itender.easyexcel.mapper.UserMapper;
import com.itender.easyexcel.pojo.User;
import com.itender.easyexcel.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import java.io.InputStream;
import java.util.*;

/**
 * @author itender
 * @date 2023/1/30 17:43
 * @desc
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void exportUserInfo(ServletOutputStream outputStream) {
        // 第一种方式
        ExcelWriter excelWriter = EasyExcelFactory.write(outputStream).build();
        WriteSheet userSheet = EasyExcelFactory.writerSheet(0)
                .head(User.class)
                // 导出文件需不包含的列名
                .excludeColumnFieldNames(Lists.newArrayList())
                // 导出文件包含的列名
                .includeColumnFieldNames(Lists.newArrayList())
                .build();
        excelWriter.write(this::getUserList, userSheet);
        excelWriter.finish();
        // 第二种方式
        EasyExcelFactory.write(outputStream, User.class).sheet("userInfo").doWrite(this::getUserList);
    }

    private List<User> getUserList() {
        return Collections.singletonList(User.builder()
                .id(1L).userName("itender").gender("男").address("广东深圳").email("itender@163.com")
                .phoneNumber(13156777777L).description("hello world")
                .build());
    }

    @Override
    public void importUserInfo(InputStream inputStream) {
        // 第一种方式
        ExcelDataListener excelDataListener = new ExcelDataListener();
        ExcelReader excelReader = EasyExcelFactory.read(inputStream).build();
        ReadSheet userSheet = EasyExcelFactory.readSheet(0)
                .head(User.class)
                .registerReadListener(excelDataListener)
                .build();
        excelReader.read(userSheet);
        // 第二种方式
        EasyExcelFactory.read(inputStream, User.class, new ReadListener<User>() {
            /**
             * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
             */
            private static final int BATCH_COUNT = 100;
            /**
             * 缓存的数据
             */
            private final List<User> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

            @Override
            public void invoke(User user, AnalysisContext analysisContext) {
                cachedDataList.add(user);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                cachedDataList.forEach(user -> log.info(user.toString()));
            }
        }).sheet().doRead();
        // 拿到错误信息，返回前端
        String errorMsg = excelDataListener.getErrorMsg();
    }

    @Override
    public void fill(ServletOutputStream outputStream) {
        // 确保文件可访问，这个例子的excel模板，放在根目录下面
        String templateFileName = "E:\\chromeDownload\\user_excel_template.xlsx";
        // 方案1
        try (ExcelWriter excelWriter = EasyExcelFactory.write(outputStream).withTemplate(templateFileName).build()) {
            WriteSheet writeSheet = EasyExcelFactory.writerSheet().build();
            FillConfig fillConfig = FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();
            // 如果有多个list 模板上必须有{前缀.} 这里的前缀就是 userList，然后多个list必须用 FillWrapper包裹
            excelWriter.fill(new FillWrapper("userList", getUserList()), fillConfig, writeSheet);
            excelWriter.fill(new FillWrapper("userList", getUserList()), fillConfig, writeSheet);
            excelWriter.fill(new FillWrapper("userList2", getUserList()), writeSheet);
            excelWriter.fill(new FillWrapper("userList2", getUserList()), writeSheet);
            Map<String, Object> map = new HashMap<>(16);
            map.put("user", "pdai");
            map.put("date", new Date());
            excelWriter.fill(map, writeSheet);
        }
    }

    @Override
    public List<User> userList() {
        return userMapper.selectList(new QueryWrapper<>());
    }
}
