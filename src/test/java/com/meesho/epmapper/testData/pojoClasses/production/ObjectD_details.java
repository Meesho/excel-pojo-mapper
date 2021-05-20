package com.meesho.epmapper.testData.pojoClasses.production;

public class ObjectD_details {


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ObjectD_details{");
        sb.append("value1='").append(value1).append('\'');
        sb.append(", value2='").append(value2).append('\'');
        sb.append('}');
        return sb.toString();
    }

    private String value1;

    public String getValue1() {
        return value1;
    }

    public ObjectD_details setValue1(String value1) {
        this.value1 = value1;
        return this;
    }

    public String getValue2() {
        return value2;
    }

    public ObjectD_details setValue2(String value2) {
        this.value2 = value2;
        return this;
    }

    private String value2;


}
