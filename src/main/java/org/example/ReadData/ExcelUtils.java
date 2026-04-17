
package org.example.ReadData;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;

public class ExcelUtils {
    public static FileOutputStream fo;
    public static FileInputStream fi;
    public static XSSFWorkbook wb;
    public static XSSFSheet sh;
    public static XSSFRow row;
    public static XSSFCell cell;
    public static CellStyle style;


    public static Object[][] getTestData(String filePath, String sheetName, int colCount) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                System.out.println("[ERROR] Sheet '" + sheetName + "' not found in " + filePath);
                workbook.close();
                fis.close();
                return null;
            }

            int rowCount = sheet.getLastRowNum();
            Object[][] data = new Object[rowCount][colCount];

            for (int i = 1; i <= rowCount; i++) {
                for (int j = 0; j < colCount; j++) {
                    XSSFCell cell = sheet.getRow(i).getCell(j);
                    data[i - 1][j] = (cell != null) ? cell.toString() : "";
                }
            }
            workbook.close();
            fis.close();
            return data;
        } catch (Exception e) {
            System.out.println("[ERROR] getTestData failed for sheet '" + sheetName + "': " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }



    public static void setCellDatas(String xlfile, int xlsheet, int rownum, int colnum, String data) throws IOException {
        fi = new FileInputStream(xlfile);
        wb = new XSSFWorkbook(fi);
        sh = wb.getSheetAt(xlsheet);

        if (sh == null) {
            sh = wb.createSheet("sheet1");
        }
        if (sh.getRow(rownum) == null) {
            row = sh.createRow(rownum);
        } else {
            row = sh.getRow(rownum);
        }
        if (row.getCell(colnum) == null) {
            cell = row.createCell(colnum);
        } else {
            cell = row.getCell(colnum);
        }
        if (data.equals(" ")) {
            cell.setCellValue("null");
        } else {
            cell.setCellValue(data);
        }
        fo = new FileOutputStream(xlfile);
        wb.write(fo);
        wb.close();
        fo.close();
        fi.close();
    }

    public static void fillGreenColor(String xlfile, String xlsheet, int rownum, int colnum) throws IOException {
        fi = new FileInputStream(xlfile);
        wb = new XSSFWorkbook(fi);
        sh = wb.getSheet(xlsheet);
        row = sh.getRow(rownum);
        cell = row.getCell(colnum);

        style = wb.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        cell.setCellStyle(style);
        fo = new FileOutputStream(xlfile);
        wb.write(fo);
        wb.close();
        fo.close();
        fi.close();
    }

    public static void fillRedColor(String xlfile, String xlsheet, int rownum, int colnum) throws IOException {
        fi = new FileInputStream(xlfile);
        wb = new XSSFWorkbook(fi);
        sh = wb.getSheet(xlsheet);
        row = sh.getRow(rownum);
        cell = row.getCell(colnum);

        style = wb.createCellStyle();
        style.setFillForegroundColor(IndexedColors.RED.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        cell.setCellStyle(style);
        fo = new FileOutputStream(xlfile);
        wb.write(fo);
        wb.close();
        fo.close();
        fi.close();
    }
    public static String getCellData(String xlfile, String xlsheet, int rownum, int colnum) throws IOException {
        fi=new FileInputStream(xlfile);
        wb=new XSSFWorkbook(fi);
        sh=wb.getSheet(xlsheet);
        row=sh.getRow(rownum);
        if(row==null){
            wb.close();
            fi.close();
            return "";
        }
        cell=row.getCell(colnum);
        if(cell==null){
            wb.close();
            fi.close();
            return "";
        }
        String data;
        try{
            DataFormatter formatter=new DataFormatter();
            data=formatter.formatCellValue(cell);
        }catch(Exception e){
            data="";
        }
        wb.close();
        fi.close();
        return data;
    }
}