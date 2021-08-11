package com.meesho.epmapper;

import com.google.gson.Gson;
import com.meesho.epmapper.testData.JsonData;
import org.testng.annotations.Test;

import java.util.List;

public class MappingTest extends BaseTest {

    @Test
    public void nestedArrayMappingTest() {
        excelObjectMapper = builder.fileLocation(getExcelLocation("NestedArray.xlsx")).build();
        ExcelObjectMapperHelper.setObjectMapper(excelObjectMapper);
        validate(ExcelObjectMapperHelper.getData("key1"), JsonData.NESTED_ARRAY_JSON.getDataForKey("key1"));
        validate(ExcelObjectMapperHelper.getData("key2"), JsonData.NESTED_ARRAY_JSON.getDataForKey("key2"));
        validate(ExcelObjectMapperHelper.getData("key3"), JsonData.NESTED_ARRAY_JSON.getDataForKey("key3"));
    }

    @Test
    public void nestedListTest() {
        excelObjectMapper = builder.fileLocation(getExcelLocation("NestedList.xlsx")).build();
        ExcelObjectMapperHelper.setObjectMapper(excelObjectMapper);
        validate(ExcelObjectMapperHelper.getData("key1"), JsonData.NESTED_ARRAY_JSON.getDataForKey("key1"));
    }

    @Test
    public void basketMappingTest() {
        excelObjectMapper = builder.fileLocation(getExcelLocation("Basket.xlsx")).build();
        ExcelObjectMapperHelper.setObjectMapper(excelObjectMapper);
        validate(ExcelObjectMapperHelper.getData("key1"), JsonData.BASKET_JSON.getDataForKey("key1"));
    }

    @Test
    public void productionMappingTest() {
        excelObjectMapper = builder.fileLocation(getExcelLocation("Production.xlsx")).build();
        ExcelObjectMapperHelper.setObjectMapper(excelObjectMapper);
        validate(ExcelObjectMapperHelper.getData("key1"), JsonData.PRODUCTION_JSON.getDataForKey("key1"));
    }

    @Test
    public void searchTest() {
        excelObjectMapper = builder.fileLocation(getExcelLocation("SearchRequestTest.xlsx")).build();
        ExcelObjectMapperHelper.setObjectMapper(excelObjectMapper);
        List<Object> data = ExcelObjectMapperHelper.getData("Test1");
        System.out.println(new Gson().toJson(data.get(0)));
//        validate(ExcelObjectMapperHelper.getData("key1"), JsonData.PRODUCTION_JSON.getDataForKey("key1"));
    }
}
