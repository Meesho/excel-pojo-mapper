package com.meesho.epmapper;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ExcelObjectMapper {
    private String fileLocation;
    private String sheetName;
    private String rootPackage;
}
