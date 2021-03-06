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
import java.util.Optional;

public class ExcelUtil {

    private static final int HEADER_ROW_INDEX = 0;
    private static final int DATA_TYPE_ROW_INDEX = 1;
    private static XSSFSheet sheet;
    private static XSSFWorkbook template;

    /**
     * @param sheet Sheet in excel file
     */
    public static void setSheet(XSSFSheet sheet) {
        ExcelUtil.sheet = sheet;
    }

    /**
     * @param excelLocation Path of excel file
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
     *
     * @param file      Input stream for specified excel file
     * @param sheetName Name of sheet which has to be created
     * @return
     */
    public static XSSFSheet createSheet(FileInputStream file, String sheetName) {
        XSSFWorkbook workbook = getWorkbook(file);
        template = workbook;
        return workbook.createSheet(sheetName);
    }

    /**
     * Create new workbook with new sheet
     *
     * @param sheetName Name of sheet which has to be created
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
     *
     * @param excelLocation Path of excel file
     */
    public static void writeToExcel(String excelLocation) {
        writeToExcel(excelLocation, template);
    }

    /**
     * Write to excel file
     *
     * @param excelLocation Path of excel file
     */
    public static void writeToExcel(String excelLocation, XSSFWorkbook workbook) {
        //Write the workbook in file system
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(excelLocation));
            workbook.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            throw new EpmapperInstantiationException("File not found in location: " + excelLocation, e.getCause());
        } catch (IOException e) {
            throw new EpmapperInstantiationException("Can not open file to write in location: " + excelLocation, e.getCause());
        }
    }

    /**
     * @param rowIndex Index of row for which last index of last cell has to be find
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
        return sheet.getRow(HEADER_ROW_INDEX);
    }

    /**
     * @return Data type row contains field name & type of pojo classes
     */
    public static XSSFRow getDataTypeRow() {
        return sheet.getRow(DATA_TYPE_ROW_INDEX);
    }

    /**
     * @return Last cell in data type row contains field name & type of pojo classes
     */
    public static int getLastCellInDataTypeRow() {
        return getDataTypeRow().getLastCellNum();
    }


    /**
     * @param cell      Cell from which data has to be fetched
     * @param delimeter Delimeter to split fetched data
     * @param remove    remove string from cell content
     * @return Array of string after removing string not required and splited based on delimeter
     */
    public static String[] getSplitedCellValue(Cell cell, String delimeter, String... remove) {
        String filterd = null;
        cell.setCellType(CellType.STRING);
        filterd = cell.getStringCellValue();
        if (remove != null && remove.length > 0)
            for (String clean : remove)
                filterd = filterd.replace(clean, "");
        return filterd.split(delimeter);
    }

    /**
     * @param cell Cell which is blank or not
     * @return true if cell is not blank
     */
    public static boolean checkIfCellIsNotBlank(Cell cell) {
        return Optional.ofNullable(cell).filter(rowCell -> StringUtils.isNotBlank(rowCell.toString())).map(Cell::getCellType).filter(cellType -> cellType != CellType.BLANK).isPresent();
    }

    /**
     * @param row Row which is empty or not
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
