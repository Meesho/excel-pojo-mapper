package com.meesho.epmapper.testData.pojoClasses.nestedList;

public class Check {

    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Check{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
