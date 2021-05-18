package com.meesho.epmapper.dataModels;

import java.util.LinkedHashMap;

public class TestData {

    public String className;
    public int startColIndex;
    public int endColIndex;
    public LinkedHashMap<String, String> fields;

    @Override
    public String toString() {
        return "TestData{" +
                "className='" + className + '\'' +
                ", startColIndex=" + startColIndex +
                ", endColIndex=" + endColIndex +
                ", fields=" + fields +
                '}';
    }
}
