package com.meesho.epmapper.testData.pojoClasses.production;

public class ObjectC_details {

    private String value1;

    private String value2;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ObjectC_details{");
        sb.append("value1='").append(value1).append('\'');
        sb.append(", value2='").append(value2).append('\'');
        sb.append(", objectF_details=").append(objectF_details);
        sb.append(", objectG_details=").append(objectG_details);
        sb.append('}');
        return sb.toString();
    }

    private ObjectF_details objectF_details;

    public String getValue1() {
        return value1;
    }

    public ObjectC_details setValue1(String value1) {
        this.value1 = value1;
        return this;
    }

    public String getValue2() {
        return value2;
    }

    public ObjectC_details setValue2(String value2) {
        this.value2 = value2;
        return this;
    }

    public ObjectF_details getObjectF_details() {
        return objectF_details;
    }

    public ObjectC_details setObjectF_details(ObjectF_details objectF_details) {
        this.objectF_details = objectF_details;
        return this;
    }

    public ObjectG_details getObjectG_details() {
        return objectG_details;
    }

    public ObjectC_details setObjectG_details(ObjectG_details objectG_details) {
        this.objectG_details = objectG_details;
        return this;
    }

    private ObjectG_details objectG_details;


}
