package com.meesho.epmapper;

import com.google.gson.Gson;
import com.meesho.epmapper.utils.ExcelUtil;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;
import static com.meesho.epmapper.ExcelObjectMapper.ExcelObjectMapperBuilder;
import static com.meesho.epmapper.utils.ExcelUtil.*;

public class BaseTest {
    private static final String testString = "Test";
    private static final int rowIndex = 2;
    private static final int colIndex = 0;
    protected static String excelLocation;
    protected ExcelObjectMapperBuilder builder;
    protected ExcelObjectMapper excelObjectMapper;
    private String rootPackage = "com.meesho.epmapper";
    private String projectLocation = System.getProperty("user.dir");
    private Gson gson = new Gson();

    protected String getRootClass(String classPath) {
        return rootPackage + "." + classPath;
    }

    protected String getExcelLocation(String relativePath) {
        return projectLocation + "/ExcelTestData/" + relativePath;
    }

    @BeforeClass
    public void setUp() {
        builder = ExcelObjectMapper.builder().
                rootPackage(this.getClass().getPackage().getName())
                .sheetName("testData");
    }

    protected String getJsonString(Object object) {
        return gson.toJson(object);
    }

    protected void validate(Object expected, Object actual) {
        try {
            JSONAssert.assertEquals("Data not matched.Expected: " + getJsonString(expected) + " found: " + getJsonString(actual), getJsonString(expected), getJsonString(actual), JSONCompareMode.STRICT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void validate(List<Object> expected, List<Object> actual) {
        if (expected == null | actual == null)
            Assert.fail();
        if (expected.size() != actual.size())
            Assert.fail();
        else
            IntStream.range(0, expected.size()).forEach(index -> validate(expected.get(index), actual.get(index)));

    }

    protected String getTestFileName(String file) {
        excelLocation = file + testString + ".xlsx";
        return excelLocation + ":testData";
    }

    private void writeToFile(String excelLocation, String sheetName) {
        FileInputStream inputStream = getExcelFileStream(excelLocation);
        XSSFWorkbook workbook = getWorkbook(inputStream);
        XSSFSheet sheet = getSheet(workbook, sheetName);
        ExcelUtil.setSheet(sheet);
        XSSFRow dataType = getDataTypeRow();
        HashMap<Integer, String> indexTypeMap = new HashMap<>();
        XSSFRow row = sheet.createRow(rowIndex);
        IntStream.range(1, getLastCellNoInRow(1)).forEach(index -> {
            indexTypeMap.put(index, dataType.getCell(index).getStringCellValue());
            row.createCell(index);
        });

        XSSFCell cell = row.createCell(colIndex);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(testString);
        indexTypeMap.entrySet().stream().forEach(integerStringEntry -> {
            XSSFCell dataCell = row.getCell(integerStringEntry.getKey());
            String[] content = integerStringEntry.getValue().split(":");
            setCellData(dataCell, content[1]);
        });
        writeToExcel(excelLocation, workbook);
    }

    protected void setTestData(String root, String path) {
        Generator generator = new Generator();
        generator.generate(root, path);
        writeToFile(getExcelLocation(excelLocation), "testData");
    }

    protected List<Object> getData() {
        excelObjectMapper = builder.fileLocation(getExcelLocation(excelLocation)).build();
        ExcelObjectMapperHelper.setObjectMapper(excelObjectMapper);
        List<Object> dataList = ExcelObjectMapperHelper.getData(testString);
        return dataList;
    }

    private void setCellData(XSSFCell cell, String type) {
        boolean isArray = false;
        if (type.contains("[L[L"))
            isArray = true;
        if (type.contains("String")) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue("random String");
        } else if (type.equalsIgnoreCase("Long") | type.equalsIgnoreCase("Double")) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(12345);
        } else if (type.equals("Boolean")) {
            cell.setCellType(CellType.BOOLEAN);
            cell.setCellValue(true);
        }

        if (isArray) {
            cell.setCellType(CellType.STRING);
            String value = cell.getStringCellValue();
            cell.setCellValue(value + ",");
        }
    }

    public void deleteFile() {
        File file = new File(getExcelLocation(excelLocation));
        file.delete();
    }
}
