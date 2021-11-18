package com.meesho.epmapper.testData.pojoClasses.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SortOption {

    private String sort_by;
    private String sort_order;
    private String display_name;
    private Boolean selected;
    @JsonProperty("default")
    @SerializedName(value = "default")
    private Boolean default1;

}
