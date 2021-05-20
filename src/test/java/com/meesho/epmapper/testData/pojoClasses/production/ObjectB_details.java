package com.meesho.epmapper.testData.pojoClasses.production;

public class ObjectB_details {

    private String value1;

    private String name;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ObjectB_details{");
        sb.append("value1='").append(value1).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", value2='").append(value2).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getValue1() {
        return value1;
    }

    public ObjectB_details setValue1(String value1) {
        this.value1 = value1;
        return this;
    }

    public String getName() {
        return name;
    }

    public ObjectB_details setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue2() {
        return value2;
    }

    public ObjectB_details setValue2(String value2) {
        this.value2 = value2;
        return this;
    }

    private String value2;


}
