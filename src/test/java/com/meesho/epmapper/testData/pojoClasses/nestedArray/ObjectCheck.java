package com.meesho.epmapper.testData.pojoClasses.nestedArray;


import java.util.Arrays;

public class ObjectCheck {
    private String percentage;

    private String type;

    private Check[] check;

    public Check[] getCheck() {
        return check;
    }

    public void setCheck(Check[] check) {
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
                ", check=" + Arrays.toString(check) +
                '}';
    }
}
