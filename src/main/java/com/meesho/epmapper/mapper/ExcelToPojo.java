package com.meesho.epmapper.mapper;

import com.meesho.epmapper.dataModels.RowData;
import com.meesho.epmapper.dataModels.TestData;
import com.meesho.epmapper.utils.Helper;
import com.meesho.epmapper.utils.ReflectUtil;
import com.meesho.epmapper.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.IntStream;
import static com.meesho.epmapper.utils.ExcelUtil.*;


public class ExcelToPojo {

    private XSSFSheet sheet;
    private TestData testData;
    private List<TestData> testDataList;
    private List<RowData> rowDataList;
    private List<List<RowData>> dataList;
    private HashMap<Field,Object> fieldObjectMap = new LinkedHashMap<>();
    private List<Object> objectList = new ArrayList<>();
    private ExcelMapper mapper = new ExcelMapper();
    private String fieldTypeSplitdelimeter = ":";
    private String arraySplitdelimeter = ",";



    public ExcelToPojo(String excelLocation, String sheetName) {
        FileInputStream inputStream = getExcelFileStream(excelLocation);
        sheet = getSheet(inputStream,sheetName);
        setSheet(sheet);
    }

    public Object getPojo(int startIndex, int endIndex) {
        createTestDataStructure(startIndex, endIndex);
        Object object = mapping(0);
        boolean isListObject = Optional.ofNullable(object).filter(listObject -> listObject instanceof List).isPresent();
        if(isListObject)
            object = arrayHandle(Utils.castToList(object));
        return object;
    }



    private void setFieldTypeMap(){
        IntStream.rangeClosed(testData.startColIndex, testData.endColIndex).forEach(j -> {
            String[] fieldName = getSplitedCellValue(getDataTypeRow().getCell(j),fieldTypeSplitdelimeter);
            testData.fields.put(fieldName[0], fieldName[1]);
        });
    }

    private void createTestDataStructure(int start, int end) {
        dataList = new ArrayList<>();int i = 1;
        testDataList = new ArrayList<TestData>();
        while (i<getLastCellInDataTypeRow()){
            testData = new TestData();
            testData.fields = new LinkedHashMap<>();
            String[] className = getSplitedCellValue(getHeaderRow().getCell(i),fieldTypeSplitdelimeter);
            testData.className = className[0].trim();
            testData.startColIndex = Integer.parseInt(className[1]);
            testData.endColIndex = Integer.parseInt(className[2]);
            setFieldTypeMap();
            i = testData.endColIndex;
            testDataList.add(testData);
            List<RowData> data = createData(start, end);
            dataList.add(data);
            i++;
        }
    }



    private boolean checkIfRowIsEmpty (Row row){
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

    public int getNumberOfRows() {
        boolean status = false;int i = 2;
        while (i < sheet.getLastRowNum() + 500){
            boolean prevStatus = status;
            status = !checkIfRowIsNotEmpty(sheet.getRow(i));
            if ((prevStatus == status) && status) break;
            i++;
        }
        return i;
    }

    public Map<String,List<Integer[]>> getDataInfoForMethod(){
        String methodName = null;
        String prev = null;
        List<Integer> info = new ArrayList<>();
        Map<String,List<Integer>> dataInfo = new LinkedHashMap<>();
        Map<String,List<Integer[]>> testDataInfo = new LinkedHashMap<>();
        int noOfRows = getNumberOfRows();
        for (int i=2;i<=noOfRows;i++){
            XSSFRow row = sheet.getRow(i);
            if(!checkIfRowIsEmpty(row)){
                XSSFCell cell = row.getCell(0);
                if (cell != null && cell.getCellType() != CellType.BLANK && StringUtils.isNotBlank(cell.toString())) {
                    prev = methodName;
                    methodName = cell.getStringCellValue();
                    if(!methodName.equals(prev) && prev != null){
                        dataInfo.put(prev,info);
                        info = new ArrayList<>();
                    }
                }
            }else {
                info.add(i);
            }

        }
        dataInfo.put(methodName,info);
        int i = 2;

        for(String s:dataInfo.keySet()){
            List<Integer> integerList = dataInfo.get(s);
            List<Integer[]> integers = new ArrayList<>();
            for(Integer j:integerList){
                boolean shouldNotAdd = (i==j) && checkIfRowIsEmpty(sheet.getRow(i));
                if(!shouldNotAdd){
                    integers.add(new Integer[]{i,j});
                }
                i = j+1;
            }
            testDataInfo.put(s,integers);
        }
        return testDataInfo;
    }

    private List<HashMap<String,Object>> createCellData(RowData rowData,Row currentRow,Row dataTypeRow,int columnIndex){
        HashMap<String, Object> map = new HashMap<>();
        Cell currentCell = currentRow.getCell(columnIndex);
        String[] fieldType = getSplitedCellValue(dataTypeRow.getCell(columnIndex),fieldTypeSplitdelimeter);
        if (checkIfCellIsNotBlank(currentCell)) {
            boolean isArrayField = Optional.of(fieldType[1]).filter(key -> key.contains("Array")).isPresent();
            String type = Helper.fieldType(fieldType[1]);
            if(isArrayField){
                boolean isCellContentIsEnd = Optional.of(currentCell.getStringCellValue()).filter(content -> content.contains("END")).isPresent();
                if(!isCellContentIsEnd){
                    map.put(fieldType[0], Helper.ArrayValue.valueOf(type).value(getSplitedCellValue(currentCell,arraySplitdelimeter)));
                }else {
                    map.put(fieldType[0],"END");
                }
            }else {
                map.put(fieldType[0], Helper.Value.valueOf(type).value(currentCell));
            }
        }
        if (!map.isEmpty())
            rowData.rowData.add(map);
        return rowData.rowData;
    }

    private RowData createRowData(Row currentRow,Row dataTypeRow,int startColumn,int endColum){
        RowData rowData = new RowData();
        rowData.rowData = new ArrayList<>();
        IntStream.rangeClosed(startColumn,endColum).forEach(column -> {rowData.rowData = createCellData(rowData,currentRow,dataTypeRow,column);});
        return rowData;
    }

    private List<RowData> createData(int start, int end){
        rowDataList = new ArrayList<>();
        IntStream.range(start,end).forEach(rowIndex -> {
            Row currentRow = sheet.getRow(rowIndex);
            if (checkIfRowIsNotEmpty(currentRow)){
                RowData rowData = createRowData(currentRow,getDataTypeRow(),testData.startColIndex,testData.endColIndex);
                if (rowData.rowData.size() > 0)
                    rowDataList.add(rowData);
            }
        });
        return rowDataList;
    }


    private Object mapping(int rootIndex){
        mapper.setMapperData(testDataList, dataList, objectList, fieldObjectMap);
        return mapper.mapping(rootIndex);
    }

    private Object arrayHandle(List<Object> list) {
        if (fieldObjectMap.size() > 0 && list != null) {
            ArrayList<Field> keys = new ArrayList<Field>(fieldObjectMap.keySet());
            int last = fieldObjectMap.size() - 1;
            for (int i = last; i >= 0; i--) {
                if ((i == last) && list.size() > 0) {
                    ReflectUtil.setArrayField(keys.get(i),fieldObjectMap.get(keys.get(i)),list);
                } else {
                    ReflectUtil.setFieldData(keys.get(i),fieldObjectMap.get(keys.get(i)),fieldObjectMap.get(keys.get(i + 1)));
                }
            }
            return fieldObjectMap.get(keys.get(0));
        }
        return list;
    }

}
