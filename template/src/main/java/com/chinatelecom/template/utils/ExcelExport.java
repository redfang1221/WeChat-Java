package com.chinatelecom.template.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * Created by HP on 2020/7/1.
 */
public class ExcelExport {
    /**
     * 导出Excel
     * @param sheetName sheet名称
     * @param title 标题
     * @param values 内容
     * @return
     */
    public static SXSSFWorkbook createWorkbook(String sheetName, String []title, String [][]values){

        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        SXSSFWorkbook wb = new SXSSFWorkbook();

        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        Sheet sheet = wb.createSheet(sheetName);

        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        Row row = sheet.createRow(0);

        // 第四步，创建单元格，并设置值表头 设置表头居中
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

        //声明列对象
        SXSSFCell cell = null;

        //创建标题
        for(int i=0;i<title.length;i++){
            cell = (SXSSFCell) row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }

        //创建内容
        for(int i=0;i<values.length;i++){
            row = sheet.createRow(i + 1);
            for(int j=0;j<values[i].length;j++){
                //将内容按顺序赋给对应的列对象
                row.createCell(j).setCellValue(values[i][j]);
            }
        }
        return wb;
    }
}
