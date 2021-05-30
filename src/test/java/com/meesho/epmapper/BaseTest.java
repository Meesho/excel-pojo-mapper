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
    private static final String TEST_STRING = "Test";
    private static final int ROW_INDEX = 2;
    private static final int COLUMN_INDEX = 0;
    private static final String SHEET_NAME = "testData";
    public static final String DEFAULT_DATA_SET = "default";
    protected String excelLocation;
    protected ExcelObjectMapperBuilder builder;
    protected ExcelObjectMapper excelObjectMapper;
    private String rootPackage = "com.meesho.epmapper";
    private String projectLocation = System.getProperty("user.dir");
    private Gson gson = new Gson();
    private Generator generator = new Generator();

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
                .sheetName(SHEET_NAME);
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
        if (expected == null || actual == null)
            Assert.fail();
        if (expected.size() != actual.size())
            Assert.fail();
        else
            IntStream.range(0, expected.size()).forEach(index -> validate(expected.get(index), actual.get(index)));

    }

    protected String getTestFileName(String file) {
        excelLocation = file + TEST_STRING + ".xlsx";
        return excelLocation + ":"+SHEET_NAME;
    }

    private void writeToFile(String excelLocation, String sheetName) {
        FileInputStream inputStream = getExcelFileStream(excelLocation);
        XSSFWorkbook workbook = getWorkbook(inputStream);
        XSSFSheet sheet = getSheet(workbook, sheetName);
        ExcelUtil.setSheet(sheet);
        XSSFRow dataType = getDataTypeRow();
        HashMap<Integer, String> indexTypeMap = new HashMap<>();
        XSSFRow row = sheet.createRow(ROW_INDEX);
        IntStream.range(1, getLastCellNoInRow(1)).forEach(index -> {
            indexTypeMap.put(index, dataType.getCell(index).getStringCellValue());
            row.createCell(index);
        });

        XSSFCell cell = row.createCell(COLUMN_INDEX);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(TEST_STRING);
        indexTypeMap.forEach((key, value) -> {
            XSSFCell dataCell = row.getCell(key);
            String[] content = value.split(":");
            setCellData(dataCell, content[1]);
        });
        writeToExcel(excelLocation, workbook);
    }

    protected void setTestData(String root, String path) {
        generator.generate(root, path);
        writeToFile(getExcelLocation(excelLocation), SHEET_NAME);
    }

    protected void setTestData(ExcelObjectMapper mapper, String relativeClassPath) {
        generator.generate(mapper, relativeClassPath);
        writeToFile(getExcelLocation(excelLocation), SHEET_NAME);
    }

    protected List<Object> getData() {
        excelObjectMapper = builder.fileLocation(getExcelLocation(excelLocation)).build();
        ExcelObjectMapperHelper.setObjectMapper(excelObjectMapper);
        return ExcelObjectMapperHelper.getData(TEST_STRING);
    }

    private void setCellData(XSSFCell cell, String type) {
        boolean isArray = false;
        if (type.contains("[L[L"))
            isArray = true;
        if (type.contains("String")) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue("random String");
        } else if (type.equalsIgnoreCase("Long") || type.equalsIgnoreCase("Double")) {
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
