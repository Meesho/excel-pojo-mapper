package com.meesho.epmapper;

import com.meesho.epmapper.testData.JsonData;
import org.testng.annotations.Test;

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
}
