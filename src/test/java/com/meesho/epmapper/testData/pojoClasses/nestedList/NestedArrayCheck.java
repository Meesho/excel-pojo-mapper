package com.meesho.epmapper.testData.pojoClasses.nestedList;

import lombok.Data;

import java.util.List;

@Data
public class NestedArrayCheck {

    private String name;

    private List<ArrayCheck> arrayCheck;

    private String description;

}
