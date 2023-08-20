package com.itender.easyexcel.interceptor;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.*;

import java.util.ArrayList;
import java.util.List;

public class HeadContentCellStyle {


    /**
     *  设置表头  和内容样式
     * @return
     */
    public static HorizontalCellStyleStrategy myHorizontalCellStyleStrategy(){
//1 表头样式策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        //设置表头居中对齐
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        //表头前景设置白色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE1.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setBold(true);
        headWriteFont.setFontName("宋体");
        headWriteFont.setFontHeightInPoints((short)11);
        headWriteCellStyle.setWriteFont(headWriteFont);

        //内容样式  多个样式则隔行换色
        List<WriteCellStyle>   listCntWritCellSty =  new ArrayList<>();

//2 内容样式策略  样式一
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        WriteFont contentWriteFont = new WriteFont();
        //内容字体大小
        contentWriteFont.setFontName("宋体");
        contentWriteFont.setFontHeightInPoints((short)11);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        //背景设置白色（这里一定要设置表格内容的背景色WPS下载下来的文件没有问题，但是office下载下来的文件表格内容会变成黑色）
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE1.getIndex());
        //设置自动换行
        contentWriteCellStyle.setWrapped(true);
        //设置垂直居中
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 头默认了 FillPatternType所以可以不指定。
        contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        //设置背景黄色
//        contentWriteCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        //设置水平靠左
        //contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        //设置边框样式
        setBorderStyle(contentWriteCellStyle);
        //内容风格可以定义多个。
        listCntWritCellSty.add(contentWriteCellStyle);

//2 内容样式策略  样式二
        WriteCellStyle contentWriteCellStyle2 = new WriteCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色。
        // 头默认了 FillPatternType所以可以不指定。
        contentWriteCellStyle2.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        //背景设置白色
        contentWriteCellStyle2.setFillForegroundColor(IndexedColors.WHITE1.getIndex());
        // 背景绿色
//        contentWriteCellStyle2.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        //设置垂直居中
        contentWriteCellStyle2.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置边框样式
        setBorderStyle(contentWriteCellStyle2);
        listCntWritCellSty.add(contentWriteCellStyle2);
        // 水平单元格风格综合策略(表头 + 内容)
        // return  new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        return  new HorizontalCellStyleStrategy(headWriteCellStyle, listCntWritCellSty);
    }

    /**
     * 设置边框样式
     * @param contentWriteCellStyle
     */
    private static void setBorderStyle(WriteCellStyle contentWriteCellStyle){
        //设置边框样式
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        // contentWriteCellStyle.setBottomBorderColor(IndexedColors.BLUE.getIndex()); //颜色
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
    }

}


