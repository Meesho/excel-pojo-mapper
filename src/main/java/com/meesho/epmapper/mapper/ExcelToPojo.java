package com.meesho.epmapper.mapper;

import com.meesho.epmapper.handler.DataHandler;
import com.meesho.epmapper.utils.ReflectUtil;
import com.meesho.epmapper.utils.Utils;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.*;

import static com.meesho.epmapper.utils.ExcelUtil.*;


public class ExcelToPojo {

    private XSSFSheet sheet;
    private HashMap<Field, Object> fieldObjectMap = new LinkedHashMap<>();
    private List<Object> objectList = new ArrayList<>();
    private ExcelMapper mapper = new ExcelMapper();
    private DataHandler dataHandler;


    public ExcelToPojo(String excelLocation, String sheetName) {
        FileInputStream inputStream = getExcelFileStream(excelLocation);
        sheet = getSheet(inputStream, sheetName);
        setSheet(sheet);
        this.dataHandler = new DataHandler();
    }

    /**
     * @param startIndex starting row index of data
     * @param endIndex   end index of row data
     * @return object with data specified between startIndex & endIndex
     */
    public Object getPojo(int startIndex, int endIndex) {
        dataHandler.createTestDataStructure(startIndex, endIndex, sheet);
        Object object = mapping();
        boolean isListObject = Optional.ofNullable(object).filter(listObject -> listObject instanceof List).isPresent();
        if (isListObject)
            object = arrayHandle(Utils.castToList(object));
        return object;
    }

    /**
     * @return row index range for data key
     */
    public Map<String, List<Integer[]>> getDataForKey() {
        Map<String, List<Integer>> dataInfo = dataHandler.getDataInfo(sheet);
        return dataHandler.getDataInfoForKey(dataInfo, sheet);
    }


    /**
     * Mapping excel data with java objects
     *
     * @return mapped data object
     */
    private Object mapping() {
        mapper.setMapperData(dataHandler.getTestDataList(), dataHandler.getDataList(), objectList, fieldObjectMap);
        return mapper.mapping(0);
    }

    /**
     * Mapping extra field in case of nested array
     *
     * @param list List of found instances
     * @return
     */
    private Object arrayHandle(List<Object> list) {
        if (fieldObjectMap.size() > 0 && list != null) {
            ArrayList<Field> keys = new ArrayList<Field>(fieldObjectMap.keySet());
            int last = fieldObjectMap.size() - 1;
            for (int i = last; i >= 0; i--) {
                if ((i == last) && list.size() > 0) {
                    ReflectUtil.setArrayField(keys.get(i), fieldObjectMap.get(keys.get(i)), list);
                } else {
                    ReflectUtil.setFieldData(keys.get(i), fieldObjectMap.get(keys.get(i)), fieldObjectMap.get(keys.get(i + 1)));
                }
            }
            return fieldObjectMap.get(keys.get(0));
        }
        return list;
    }
}
