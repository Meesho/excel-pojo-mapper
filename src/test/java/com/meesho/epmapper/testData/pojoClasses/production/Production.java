package com.meesho.epmapper.testData.pojoClasses.production;

import lombok.Data;

@Data
public class Production {

    private ObjectE_details objectE_details;

    private ObjectA_details objectA_details;

    private ObjectB_details objectB_details;

    private ObjectC_details[] objectC_details;

    private ObjectD_details objectD_details;

}
