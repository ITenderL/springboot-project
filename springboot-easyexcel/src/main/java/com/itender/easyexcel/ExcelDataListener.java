package com.itender.easyexcel;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.itender.easyexcel.pojo.User;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author itender
 * @date 2023/1/31 12:21
 * @desc
 */
@Slf4j
public class ExcelDataListener implements ReadListener<User> {

    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;
    /**
     * 缓存的数据
     */
    private static final List<User> CACHED_DATA_LIST = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    /**
     * 错误信息
     */
    @Getter
    private String errorMsg;

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param user
     * @param analysisContext
     */
    @Override
    public void invoke(User user, AnalysisContext analysisContext) {
        log.info("解析到一条数据:{}", JSONUtil.toJsonStr(user));
        // TODO 校验导入数据是否合规
        // 如果不合规
        this.errorMsg = StrFormatter.format("导入数据第{}行校验不通过！", analysisContext.readRowHolder().getRowIndex());
        CACHED_DATA_LIST.add(user);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (CACHED_DATA_LIST.size() >= BATCH_COUNT) {
            // TODO 保存数据到MySQL

            // 存储完成置空list
            CACHED_DATA_LIST.clear();
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        // TODO 保存数据到MySQL
        log.info("所有数据解析完成！");
    }
}
