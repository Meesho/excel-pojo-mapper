package com.meesho.epmapper.testData.pojoClasses.nestedList;


import java.util.Arrays;
import java.util.List;

public class ObjectCheck {
    private String percentage;

    private String type;

    private List<Check> check;

    public List<Check> getCheck() {
        return check;
    }

    public void setCheck(List<Check> check) {
        this.check = check;
    }



    public String getPercentage ()
    {
        return percentage;
    }

    public void setPercentage (String percentage)
    {
        this.percentage = percentage;
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
    public String toString() {
        return "Distribution{" +
                "percentage=" + percentage +
                ", type='" + type + '\'' +
                ", check=" + check.toString() +
                '}';
    }
}
