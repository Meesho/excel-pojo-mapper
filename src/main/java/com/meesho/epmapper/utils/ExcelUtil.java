package com.meesho.epmapper.utils;

import com.meesho.epmapper.exceptions.EpmapperInstantiationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExcelUtil {

    private static final int headerRowNo = 0;
    private static final int dataTypeRowNo = 1;
    private static XSSFSheet sheet;
    private static XSSFWorkbook template;

    /**
     * @param sheet
     */
    public static void setSheet(XSSFSheet sheet) {
        ExcelUtil.sheet = sheet;
    }

    /**
     * @param excelLocation Location of Excel sheet
     * @return FileInputStream
     */
    public static FileInputStream getExcelFileStream(String excelLocation) {
        File file = new File(excelLocation);
        FileInputStream fileInputStream = null;
        try {
            if (file.exists())
                fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new EpmapperInstantiationException("File not found in location: " + excelLocation, e.getCause());
        }
        return fileInputStream;
    }

    public static XSSFWorkbook getWorkbook(FileInputStream file) {
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(file);
        } catch (IOException e) {
            throw new EpmapperInstantiationException("Can not open file to read, check file extension", e.getCause());
        }
        return workbook;
    }

    public static XSSFWorkbook getWorkbook() {
        template = new XSSFWorkbook();
        return template;
    }

    /**
     * Create sheet in existing file
     * @param file
     * @param sheetName
     * @return
     */
    public static XSSFSheet createSheet(FileInputStream file, String sheetName) {
        XSSFWorkbook workbook = getWorkbook(file);
        template = workbook;
        return workbook.createSheet(sheetName);
    }

    /**
     * Create new workbook with new sheet
     * @param sheetName
     * @return
     */
    public static XSSFSheet createSheet(String sheetName) {
        XSSFWorkbook workbook = getWorkbook();
        return workbook.createSheet(sheetName);
    }

    /**
     * @param file      FileInputStream for excel
     * @param sheetName Name of sheet in excel
     * @return sheet
     */
    public static XSSFSheet getSheet(FileInputStream file, String sheetName) {
        XSSFWorkbook workbook = getWorkbook(file);
        return workbook.getSheet(sheetName);
    }


    public static XSSFSheet getSheet(XSSFWorkbook workbook, String sheetName) {
        return workbook.getSheet(sheetName);
    }

    /**
     * Write to excel file
     * @param excelLocation
     */
    public static void writeToExcel(String excelLocation) {
        //Write the workbook in file system
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(excelLocation));
            template.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            throw new EpmapperInstantiationException("File not found in location: "+excelLocation,e.getCause());
        } catch (IOException e) {
            throw new EpmapperInstantiationException("Can not open file to write in location: "+excelLocation,e.getCause());
        }
    }

    /**
     * Write to excel file
     * @param excelLocation
     */
    public static void writeToExcel(String excelLocation,XSSFWorkbook workbook) {
        //Write the workbook in file system
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(excelLocation));
            workbook.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            throw new EpmapperInstantiationException("File not found in location: "+excelLocation,e.getCause());
        } catch (IOException e) {
            throw new EpmapperInstantiationException("Can not open file to write in location: "+excelLocation,e.getCause());
        }
    }

    /**
     * @param rowIndex
     * @return Index of last cell in row
     */
    public static int getLastCellNoInRow(int rowIndex) {
        XSSFRow row = sheet.getRow(rowIndex);
        return row.getLastCellNum();
    }

    /**
     * @return Header row contains pojo class names
     */
    public static XSSFRow getHeaderRow() {
        return sheet.getRow(headerRowNo);
    }

    /**
     * @return Data type row contains field name & type of pojo classes
     */
    public static XSSFRow getDataTypeRow() {
        return sheet.getRow(dataTypeRowNo);
    }

    /**
     * @return Last cell in data type row contains field name & type of pojo classes
     */
    public static int getLastCellInDataTypeRow() {
        return getDataTypeRow().getLastCellNum();
    }

    /**
     * @param cell
     * @param delimeter
     * @return Cell content in array based on split delimeter
     */
    public static String[] getSplitedCellValue(Cell cell, String delimeter,String... remove) {
        String filterd = null;
        cell.setCellType(CellType.STRING);
        filterd = cell.getStringCellValue();
        if(remove != null && remove.length>0)
            for (String clean:remove)
            filterd = filterd.replace(clean,"");
        return filterd.split(delimeter);
    }

    /**
     * @param cell
     * @return true if cell is not blank
     */
    public static boolean checkIfCellIsNotBlank(Cell cell) {
        return Optional.ofNullable(cell).filter(rowCell -> StringUtils.isNotBlank(rowCell.toString())).map(Cell::getCellType).filter(cellType -> cellType != CellType.BLANK).isPresent();
    }

    public static List<Cell> getNonEmptyCell(Row row) {
        List<Cell> cells = IntStream.rangeClosed(row.getFirstCellNum(), row.getLastCellNum()).mapToObj(row::getCell).filter(ExcelUtil::checkIfCellIsNotBlank).collect(Collectors.toList());
        return cells;
    }

    /**
     * @param row
     * @return true if row is empty
     */
    public static boolean checkIfRowIsEmpty(Row row) {
        if (row == null) {
            return true;
        }
        if (row.getLastCellNum() <= 0) {
            return true;
        }
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK && StringUtils.isNotBlank(cell.toString())) {
                return false;
            }
        }
        return true;
    }
}
