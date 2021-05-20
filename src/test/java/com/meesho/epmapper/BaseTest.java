package com.meesho.epmapper;

import com.google.gson.Gson;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

import java.util.List;
import java.util.stream.IntStream;

public class BaseTest {
    private String rootPackage = "com.meesho.epmapper";
    private String projectLocation = System.getProperty("user.dir");
    protected String getRootClass(String classPath){
        return rootPackage+"."+classPath;
    }
    protected String getExcelLocation(String relativePath){
        return projectLocation+"/ExcelTestData/"+relativePath;
    }
    protected ExcelObjectMapper excelObjectMapper;
    private Gson gson = new Gson();

    @BeforeClass
    public void setUp(){
        excelObjectMapper = new ExcelObjectMapper();
        excelObjectMapper.setRootPackage(this.getClass().getPackage().getName());
        excelObjectMapper.setSheetName("testData");
    }

    protected String getJsonString(Object object){
        return gson.toJson(object);
    }

    protected void validate(Object expected,Object actual){
        try {
            JSONAssert.assertEquals("Data not matched.Expected: "+getJsonString(expected)+" found: "+getJsonString(actual),getJsonString(expected),getJsonString(actual), JSONCompareMode.STRICT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void validate(List<Object> expected, List<Object> actual){
        if(expected == null | actual == null)
            Assert.fail();
        if(expected.size() != actual.size())
            Assert.fail();
        else
            IntStream.range(0,expected.size()).forEach(index -> validate(expected.get(index),actual.get(index)));

    }

}
