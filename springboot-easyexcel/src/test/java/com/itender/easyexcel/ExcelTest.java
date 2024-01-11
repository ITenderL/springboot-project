package com.itender.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import com.google.common.collect.Lists;
import com.itender.easyexcel.interceptor.HeadContentCellStyle;
import com.itender.easyexcel.pojo.DemoData;
import com.itender.easyexcel.util.TestFileUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

/**
 * @author itender
 * @date 2023/2/16 12:41
 * @desc
 */
@SpringBootTest
public class ExcelTest {
    /**
     * 根据参数只导出指定列
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link DemoData}
     * <p>
     * 2. 根据自己或者排除自己需要的列
     * <p>
     * 3. 直接写即可
     *
     * @since 2.1.1
     */
    @Test
    void excludeOrIncludeWrite() {
        String fileName = TestFileUtil.getPath() + "excludeOrIncludeWrite" + System.currentTimeMillis() + ".xlsx";
        // 这里需要注意 在使用ExcelProperty注解的使用，如果想不空列则需要加入order字段，而不是index,order会忽略空列，然后继续往后，而index，不会忽略空列，在第几列就是第几列。

        // 根据用户传入字段 假设我们要忽略 date
        Set<String> excludeColumnFiledNames = new HashSet<>();
        excludeColumnFiledNames.add("date");
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(fileName, DemoData.class).excludeColumnFiledNames(excludeColumnFiledNames).sheet("模板")
                .doWrite(data());

        fileName = TestFileUtil.getPath() + "excludeOrIncludeWrite" + System.currentTimeMillis() + ".xlsx";
        // 根据用户传入字段 假设我们只要导出 date
        Set<String> includeColumnFiledNames = new HashSet<>();
        includeColumnFiledNames.add("date");
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(fileName, DemoData.class).includeColumnFiledNames(includeColumnFiledNames).sheet("模板")
                .doWrite(data());
    }

    private List<DemoData> data() {
        List<DemoData> list = ListUtils.newArrayList();
        for (int i = 0; i < 10; i++) {
            DemoData data = new DemoData();
            data.setString("字符串" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }

    /**
     * 动态头，实时生成头写入
     * <p>
     * 思路是这样子的，先创建List<String>头格式的sheet仅仅写入头,然后通过table 不写入头的方式 去写入数据
     *
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link DemoData}
     * <p>
     * 2. 然后写入table即可
     */
    @Test
    public void dynamicHeadWrite() {
        String fileName = "D:\\workspace\\IdeaProjects\\springboot-project\\springboot-easyexcel\\src\\main\\resources\\" + System.currentTimeMillis() + ".xlsx";
        EasyExcel.write(fileName)
                // 这里放入动态头
                .head(head()).sheet("模板")
                .registerWriteHandler(HeadContentCellStyle.myHorizontalCellStyleStrategy())
                .registerWriteHandler(new SimpleColumnWidthStyleStrategy(15)) // 简单的列宽策略，列宽15
                .registerWriteHandler(new SimpleRowHeightStyleStrategy((short)25,(short)15)) // 简单的行高策略：头行高30，内容行高15
                // 当然这里数据也可以用 List<List<String>> 去传入
                .doWrite(Lists::newArrayList);
    }

    private List<List<String>> head() {
        List<List<String>> list = new ArrayList<List<String>>();
        List<String> head0 = new ArrayList<>();
        head0.add("一级表头");
        head0.add("二级表头-字符串");
        head0.add("三级表头-字符串");
        List<String> head1 = new ArrayList<>();
        head1.add("一级表头");
        head1.add("二级表头-数字");
        head1.add("三级表头-数字");
        List<String> head2 = new ArrayList<>();
        head2.add("一级表头-日期");
        head2.add("二级表头-日期");
        head2.add("二级-日期-3");
        List<String> head3 = new ArrayList<>();
        head3.add("一级表头-日期");
        head3.add("二级表头-日期");
        head3.add("三级-日期-3");
        List<String> head4 = new ArrayList<>();
        head4.add("一级表头-日期");
        head4.add("二级表头-日期-2");
        head4.add("三级-日期");
        list.add(head0);
        list.add(head1);
        list.add(head2);
        list.add(head3);
        list.add(head4);
        return list;
    }
}
