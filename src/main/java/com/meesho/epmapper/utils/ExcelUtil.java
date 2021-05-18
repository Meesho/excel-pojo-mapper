package com.meesho.epmapper.utils;

import com.meesho.epmapper.exceptions.EpmapperInstantiationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExcelUtil {

    private static XSSFSheet sheet;
    private static final int headerRowNo = 0;
    private static final int dataTypeRowNo = 1;


    /**
     * @param sheet
     */
    public static void setSheet(XSSFSheet sheet){
        ExcelUtil.sheet = sheet;
    }

    /**
     * @param excelLocation Location of Excel sheet
     * @return FileInputStream
     */
    public static FileInputStream getExcelFileStream(String excelLocation){
        FileInputStream file = null;
        try {
            file = new FileInputStream(new File(excelLocation));
        } catch (FileNotFoundException e) {
            throw new EpmapperInstantiationException("File not found in location: "+excelLocation,e.getCause());
        }
        return file;
    }

    /**
     * @param file FileInputStream for excel
     * @param sheetName Name of sheet in excel
     * @return sheet
     */
    public static XSSFSheet getSheet(FileInputStream file,String sheetName){

        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(file);
        } catch (IOException e) {
            throw new EpmapperInstantiationException("Can not open file to read, check file extension",e.getCause());
        }
        return workbook.getSheet(sheetName);
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
    public static String[] getSplitedCellValue(Cell cell, String delimeter) {
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().split(delimeter);
    }

    public static boolean checkIfCellIsNotBlank(Cell cell){
        return Optional.ofNullable(cell).filter(rowCell -> StringUtils.isNotBlank(rowCell.toString())).map(Cell::getCellType).filter(cellType -> cellType != CellType.BLANK).isPresent();
    }

    public static List<Cell> getNonEmptyCell(Row row){
        List<Cell> cells = IntStream.rangeClosed(row.getFirstCellNum(), row.getLastCellNum()).mapToObj(row::getCell).filter(ExcelUtil::checkIfCellIsNotBlank).collect(Collectors.toList());
        return cells;
    }
    public static boolean checkIfRowIsNotEmpty(Row row) {
        return Optional.ofNullable(row).map(ExcelUtil::getNonEmptyCell).filter(nonEmptyCell -> nonEmptyCell.size()>0).isPresent();
    }
}
