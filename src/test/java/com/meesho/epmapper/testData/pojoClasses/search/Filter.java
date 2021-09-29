package com.meesho.epmapper.testData.pojoClasses.search;

import lombok.Data;

import java.util.List;

@Data
public class Filter {
    private List < String > selected_filters;
    private String type;
    private SortOption sort_option;
    private String session_state;
    private Integer catalog_listing_page_id;
    private String payload;
    private String query;
    private String image_url;
}
