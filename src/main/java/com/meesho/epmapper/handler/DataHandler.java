package com.meesho.epmapper.handler;

import com.meesho.epmapper.dataModels.RowData;
import com.meesho.epmapper.dataModels.TestData;
import com.meesho.epmapper.utils.Helper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.*;
import java.util.stream.IntStream;

import static com.meesho.epmapper.utils.ExcelUtil.*;


public class DataHandler {

    private String fieldTypeSplitdelimeter = ":";
    private String arraySplitdelimeter = ",";
    private List<List<RowData>> dataList = new ArrayList<>();
    private List<TestData> testDataList = new ArrayList<>();
    private TestData testData;

    /**
     * Second row contain field & field type (field:field type),method set these value to test data object
     *
     * @param testData Complete information about class,it's fields & column indexes.
     */
    private void setFieldTypeMap(TestData testData) {
        IntStream.rangeClosed(testData.startColIndex, testData.endColIndex).forEach(j -> {
            String[] fieldName = getSplitedCellValue(getDataTypeRow().getCell(j), fieldTypeSplitdelimeter);
            testData.fields.put(fieldName[0], fieldName[1]);
        });
    }

    /**
     * Second row contain pojo class name & column range for data,method set these value to test data object
     *
     * @param start Starting column index of class fields
     * @param end   Ending column index of class fields
     * @param sheet Sheet which data need to be mapped with pojo
     */
    public void createTestDataStructure(int start, int end, XSSFSheet sheet) {
        dataList = new ArrayList<>();
        int i = 1;
        while (i < getLastCellInDataTypeRow()) {
            testData = new TestData();
            testData.fields = new LinkedHashMap<>();
            String[] className = getSplitedCellValue(getHeaderRow().getCell(i), fieldTypeSplitdelimeter);
            testData.className = className[0].trim();
            testData.startColIndex = Integer.parseInt(className[1]);
            testData.endColIndex = Integer.parseInt(className[2]);
            setFieldTypeMap(testData);
            i = testData.endColIndex;
            testDataList.add(testData);
            List<RowData> data = createData(start, end, testData, sheet);
            dataList.add(data);
            i++;
        }
    }

    /**
     * Method find end of data kept in excel.If two consecutive row are blank,it consider end
     *
     * @param sheet Sheet which data need to be mapped with pojo
     * @return
     */
    public int getNumberOfRows(XSSFSheet sheet) {
        boolean status = false;
        int i = 2;
        while (i < sheet.getLastRowNum() + 500) {
            boolean prevStatus = status;
            status = checkIfRowIsEmpty(sheet.getRow(i));
            if ((prevStatus == status) && status) break;
            i++;
        }
        return i;
    }

    /**
     * Create data for cell with field name & it's content.We use END delimiter to separate data in case of nested list
     *
     * @param rowData     Data of complete row
     * @param currentRow  Row from where data has to be fetched
     * @param dataTypeRow Row contains field & it's type
     * @param columnIndex Index of current cell
     * @return
     */
    private List<HashMap<String, Object>> createCellData(RowData rowData, Row currentRow, Row dataTypeRow, int columnIndex) {
        HashMap<String, Object> map = new HashMap<>();
        Cell currentCell = currentRow.getCell(columnIndex);
        String[] fieldType = getSplitedCellValue(dataTypeRow.getCell(columnIndex), fieldTypeSplitdelimeter);
        if (checkIfCellIsNotBlank(currentCell)) {
            String type = Helper.fieldType(fieldType[1]);
            boolean isArrayField = Optional.of(type).filter(key -> key.contains("Array")).isPresent();
            if (isArrayField) {
                boolean isCellContentIsEnd = Optional.of(currentCell.getStringCellValue()).filter(content -> content.contains("END")).isPresent();
                if (!isCellContentIsEnd) {
                    map.put(fieldType[0], Helper.ArrayValue.valueOf(type).value(getSplitedCellValue(currentCell, arraySplitdelimeter, "[", "]")));
                } else {
                    map.put(fieldType[0], "END");
                }
            } else {
                map.put(fieldType[0], Helper.Value.valueOf(type).value(currentCell));
            }
        }
        if (!map.isEmpty())
            rowData.rowData.add(map);
        return rowData.rowData;
    }

    /**
     * @param currentRow  Row from where data has to be fetched
     * @param dataTypeRow Row contains field & it's type
     * @param startColumn Starting column index of class fields
     * @param endColum    Ending column index of class fields
     * @return Data in Row
     */
    private RowData createRowData(Row currentRow, Row dataTypeRow, int startColumn, int endColum) {
        RowData rowData = new RowData();
        rowData.rowData = new ArrayList<>();
        IntStream.rangeClosed(startColumn, endColum).forEach(column -> {
            rowData.rowData = createCellData(rowData, currentRow, dataTypeRow, column);
        });
        return rowData;
    }

    /**
     * @param start    Starting row index of data
     * @param end      Ending row index of data
     * @param testData Complete information about class,it's fields & column indexes.
     * @param sheet    Sheet which data need to be mapped with pojo
     * @return List of row's data
     */
    private List<RowData> createData(int start, int end, TestData testData, XSSFSheet sheet) {
        List<RowData> rowDataList = new ArrayList<>();
        IntStream.range(start, end).forEach(rowIndex -> {
            Row currentRow = sheet.getRow(rowIndex);
            if (!checkIfRowIsEmpty(currentRow)) {
                RowData rowData = createRowData(currentRow, getDataTypeRow(), testData.startColIndex, testData.endColIndex);
                if (rowData.rowData.size() > 0)
                    rowDataList.add(rowData);
            }
        });
        return rowDataList;
    }

    /**
     * @param sheet Sheet which data need to be mapped with pojo
     * @return row index for key
     */
    public Map<String, List<Integer>> getDataInfo(XSSFSheet sheet) {
        String keyName = null, prev = null;
        List<Integer> info = new ArrayList<>();
        Map<String, List<Integer>> dataInfo = new LinkedHashMap<>();
        int noOfRows = getNumberOfRows(sheet);
        for (int i = 2; i <= noOfRows; i++) {
            XSSFRow row = sheet.getRow(i);
            if (!checkIfRowIsEmpty(row)) {
                XSSFCell cell = row.getCell(0);
                if (checkIfCellIsNotBlank(cell)) {
                    prev = keyName;
                    keyName = cell.getStringCellValue();
                    if (!keyName.equals(prev) && prev != null) {
                        dataInfo.put(prev, info);
                        info = new ArrayList<>();
                    }
                }
            } else {
                info.add(i);
            }

        }
        dataInfo.put(keyName, info);
        return dataInfo;
    }

    /**
     * Map range of row index with data key
     *
     * @param dataInfo Range of row index for data name
     * @param sheet    Sheet which data need to be mapped with pojo
     * @return
     */
    public Map<String, List<Integer[]>> getDataInfoForKey(Map<String, List<Integer>> dataInfo, XSSFSheet sheet) {
        int i = 2;
        Map<String, List<Integer[]>> testDataInfo = new LinkedHashMap<>();
        for (String s : dataInfo.keySet()) {
            List<Integer> integerList = dataInfo.get(s);
            List<Integer[]> integers = new ArrayList<>();
            for (Integer j : integerList) {
                boolean shouldNotAdd = (i == j) && checkIfRowIsEmpty(sheet.getRow(i));
                if (!shouldNotAdd) {
                    integers.add(new Integer[]{i, j});
                }
                i = j + 1;
            }
            testDataInfo.put(s, integers);
        }
        return testDataInfo;
    }

    public List<List<RowData>> getDataList() {
        return dataList;
    }

    public List<TestData> getTestDataList() {
        return testDataList;
    }

    public TestData getTestData() {
        return testData;
    }
}
