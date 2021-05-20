package com.meesho.epmapper.testData.pojoClasses.production;

public class Production {

    private ObjectE_details objectE_details;

    private ObjectA_details objectA_details;

    private ObjectB_details objectB_details;

    private ObjectC_details[] objectC_details;

    private ObjectD_details objectD_details;

    public ObjectE_details getObjectE_details ()
    {
        return objectE_details;
    }

    public void setObjectE_details (ObjectE_details objectE_details)
    {
        this.objectE_details = objectE_details;
    }

    public ObjectA_details getObjectA_details ()
    {
        return objectA_details;
    }

    public void setObjectA_details (ObjectA_details objectA_details)
    {
        this.objectA_details = objectA_details;
    }

    public ObjectB_details getObjectB_details ()
    {
        return objectB_details;
    }

    public void setObjectB_details (ObjectB_details objectB_details)
    {
        this.objectB_details = objectB_details;
    }

    public ObjectC_details[] getObjectC_details ()
    {
        return objectC_details;
    }

    public void setObjectC_details (ObjectC_details[] objectC_details)
    {
        this.objectC_details = objectC_details;
    }

    public ObjectD_details getObjectD_details ()
    {
        return objectD_details;
    }

    public void setObjectD_details (ObjectD_details objectD_details)
    {
        this.objectD_details = objectD_details;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [objectE_details = "+objectE_details+", objectA_details = "+objectA_details+", objectB_details = "+objectB_details+", objectC_details = "+objectC_details+", objectD_details = "+objectD_details+"]";
    }
}
