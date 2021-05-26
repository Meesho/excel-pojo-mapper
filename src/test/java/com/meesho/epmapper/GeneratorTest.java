package com.meesho.epmapper;

import com.meesho.epmapper.testData.JsonData;
import org.testng.annotations.Test;
import java.util.List;

public class GeneratorTest extends BaseTest {


    @Test
    public void nestedArrayTest() {
        String root = getRootClass("testData.pojoClasses.nestedArray.NestedArrayCheck");
        String path = getExcelLocation(getTestFileName("NestedArray"));
        setTestData(root, path);
        List<Object> data = getData();
        validate(data, JsonData.NestedArrayJson.getDataForKey("default"));
        deleteFile();
    }


    @Test
    public void basketTest() {
        String root = getRootClass("testData.pojoClasses.basket.Basket");
        String path = getExcelLocation(getTestFileName("Basket"));
        setTestData(root, path);
        List<Object> data = getData();
        validate(data, JsonData.BasketJson.getDataForKey("default"));
        deleteFile();
    }

    @Test
    public void productionTest() {
        String root = getRootClass("testData.pojoClasses.production.Production");
        String path = getExcelLocation(getTestFileName("Production"));
        setTestData(root, path);
        List<Object> data = getData();
        validate(data, JsonData.ProductionJson.getDataForKey("default"));
        deleteFile();
    }

    @Test
    public void nestedArrayTestV1() {
        excelObjectMapper = builder.fileLocation(getExcelLocation(getTestFileName("NestedArray"))).build();
        setTestData(excelObjectMapper,"testData.pojoClasses.nestedArray.NestedArrayCheck");
        List<Object> data = getData();
        validate(data, JsonData.NestedArrayJson.getDataForKey("default"));
        deleteFile();
    }


    @Test
    public void basketTestV1() {
        excelObjectMapper = builder.fileLocation(getExcelLocation(getTestFileName("Basket"))).build();
        setTestData(excelObjectMapper,"testData.pojoClasses.basket.Basket");
        List<Object> data = getData();
        validate(data, JsonData.BasketJson.getDataForKey("default"));
        deleteFile();
    }

    @Test
    public void productionTestV1() {
        excelObjectMapper = builder.fileLocation(getExcelLocation(getTestFileName("Production"))).build();
        setTestData(excelObjectMapper,"testData.pojoClasses.production.Production");
        List<Object> data = getData();
        validate(data, JsonData.ProductionJson.getDataForKey("default"));
        deleteFile();
    }
}
