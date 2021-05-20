package com.meesho.epmapper.testData.pojoClasses.nestedList;

import java.util.Arrays;
import java.util.List;

public class NestedArrayCheck {

    private String name;

    private List<ArrayCheck> arrayCheck;

    private String description;

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public List<ArrayCheck> getOptions ()
    {
        return arrayCheck;
    }

    public void setOptions (List<ArrayCheck> arrayCheck)
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
                ", arrayCheck=" + arrayCheck.toString() +
                ", description='" + description + '\'' +
                '}';
    }
}
