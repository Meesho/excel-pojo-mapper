package com.meesho.epmapper;

import com.meesho.epmapper.testData.JsonData;
import org.testng.annotations.Test;

public class MappingTest extends BaseTest{

    @Test
    public void nestedArrayMappingTest() {
        excelObjectMapper.setFileLocation(getExcelLocation("NestedArray.xlsx"));
        validate(excelObjectMapper.getData("key1"),JsonData.NestedArrayJson.getDataForKey("key1"));
        validate(excelObjectMapper.getData("key2"),JsonData.NestedArrayJson.getDataForKey("key2"));
        validate(excelObjectMapper.getData("key3"),JsonData.NestedArrayJson.getDataForKey("key3"));
    }

    @Test
    public void nestedListTest() {
        excelObjectMapper.setFileLocation(getExcelLocation("NestedList.xlsx"));
        validate(excelObjectMapper.getData("key1"),JsonData.NestedArrayJson.getDataForKey("key1"));
    }

    @Test
    public void basketMappingTest() {
        excelObjectMapper.setFileLocation(getExcelLocation("Basket.xlsx"));
        validate(excelObjectMapper.getData("key1"),JsonData.BasketJson.getDataForKey("key1"));
    }

    @Test
    public void productionMappingTest() {
        excelObjectMapper.setFileLocation(getExcelLocation("Production.xlsx"));
        validate(excelObjectMapper.getData("key1"),JsonData.ProductionJson.getDataForKey("key1"));
    }
}
