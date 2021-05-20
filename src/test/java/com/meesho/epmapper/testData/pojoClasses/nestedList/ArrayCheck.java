package com.meesho.epmapper.testData.pojoClasses.nestedList;

public class ArrayCheck {

    private String id;

    private ObjectCheck objectCheck;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public ObjectCheck getDistribution ()
    {
        return objectCheck;
    }

    public void setDistribution (ObjectCheck objectCheck)
    {
        this.objectCheck = objectCheck;
    }

    @Override
    public String toString() {
        return "Options{" +
                "id='" + id + '\'' +
                ", objectCheck=" + objectCheck +
                '}';
    }
}
