package com.meesho.epmapper.testData.pojoClasses.nestedList;

import lombok.Data;

import java.util.List;

@Data
public class ObjectCheck {
    private String percentage;

    private String type;

    private List<Check> check;

}
