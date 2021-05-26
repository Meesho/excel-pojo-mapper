package com.meesho.epmapper.testData.pojoClasses.nestedArray;

import lombok.Data;

@Data
public class ObjectCheck {
    private String percentage;

    private String type;

    private Check[] check;

}
