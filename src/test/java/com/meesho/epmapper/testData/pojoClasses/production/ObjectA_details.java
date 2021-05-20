package com.meesho.epmapper.testData.pojoClasses.production;

public class ObjectA_details {

    private String name;
    private String value;

    public String getValue() {
        return value;
    }

    public ObjectA_details setValue(String value) {
        this.value = value;
        return this;
    }

    public String getName() {
        return name;
    }

    public ObjectA_details setName(String name) {
        this.name = name;
        return this;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ObjectA_details{");
        sb.append("value='").append(value).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
