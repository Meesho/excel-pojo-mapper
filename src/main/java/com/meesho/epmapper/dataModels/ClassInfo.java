package com.meesho.epmapper.dataModels;

public class ClassInfo {
    public String fieldName;
    public String fieldType;
    public String className;

    public ClassInfo(String fieldName, String fieldType, String className) {
        this.className = className;
        this.fieldType = fieldType;
        this.fieldName = fieldName;
    }
}
