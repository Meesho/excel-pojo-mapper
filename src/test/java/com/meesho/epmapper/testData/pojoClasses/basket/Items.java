package com.meesho.epmapper.testData.pojoClasses.basket;

public class Items
{
    private Data[] data;

    private String type;

    public Data[] getData ()
    {
        return data;
    }

    public void setData (Data[] data)
    {
        this.data = data;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [data = "+data+", type = "+type+"]";
    }
}