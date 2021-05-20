package com.meesho.epmapper;

import org.testng.annotations.Test;

public class GeneratorTest extends BaseTest{

    @Test
    public void nestedArrayTest() {
        String root = getRootClass("testData.pojoClasses.nestedArray.NestedArrayCheck");
        String path = getExcelLocation("NestedArray.xlsx:testData");
        Generator generator = new Generator();
        generator.generate(root,path);
    }

    @Test
    public void nestedListTest() {
        String root = getRootClass("testData.pojoClasses.nestedList.NestedArrayCheck");
        String path = getExcelLocation("NestedList.xlsx:testData");
        Generator generator = new Generator();
        generator.generate(root,path);
    }

    @Test
    public void basketTest() {
        String root = getRootClass("testData.pojoClasses.basket.Basket");
        String path = getExcelLocation("Basket.xlsx:testData");
        Generator generator = new Generator();
        generator.generate(root,path);
    }

    @Test
    public void productionTest() {
        String root = getRootClass("testData.pojoClasses.production.Production");
        String path = getExcelLocation("Production.xlsx:testData");
        Generator generator = new Generator();
        generator.generate(root,path);
    }
}
