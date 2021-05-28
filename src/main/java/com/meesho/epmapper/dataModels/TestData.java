package com.meesho.epmapper.dataModels;

import lombok.ToString;

import java.util.LinkedHashMap;

@ToString
public class TestData {
    public String className;
    public int startColIndex;
    public int endColIndex;
    public LinkedHashMap<String, String> fields;
}
