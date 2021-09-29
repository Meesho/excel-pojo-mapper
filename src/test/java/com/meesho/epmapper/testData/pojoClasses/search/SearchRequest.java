package com.meesho.epmapper.testData.pojoClasses.search;


import lombok.Data;

@Data
public class SearchRequest {

    private String description;
    private String cursor;
    private Filter filter;
    private String offset;
    private String limit;
    private String session_session_id;
    private String user_id;
}
