package com.meesho.epmapper.testData.pojoClasses.nestedArray;

import java.util.Arrays;

public class NestedArrayCheck {

    private String name;

    private ArrayCheck[] arrayCheck;

    private String description;

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public ArrayCheck[] getOptions ()
    {
        return arrayCheck;
    }

    public void setOptions (ArrayCheck[] arrayCheck)
    {
        this.arrayCheck = arrayCheck;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CreateSpin{" +
                "name='" + name + '\'' +
                ", arrayCheck=" + Arrays.toString(arrayCheck) +
                ", description='" + description + '\'' +
                '}';
    }
}
