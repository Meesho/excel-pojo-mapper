package com.meesho.epmapper;

import com.meesho.epmapper.mapper.ExcelToPojo;

import java.util.ArrayList;
import java.util.List;

public class ExcelObjectMapper {

    private String fileLocation;
    private String sheetName;
    private String rootPackage;

    public ExcelObjectMapper setRootPackage(String rootPackage) {
        this.rootPackage = rootPackage;
        return this;
    }

    public ExcelObjectMapper setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
        return this;
    }

    public ExcelObjectMapper setSheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    public Object getData(String key){
        List<Object> lines = new ArrayList<Object>();
        ExcelToPojo excelToPojo = new ExcelToPojo(fileLocation,sheetName);
        List<Integer[]> dataInfo = excelToPojo.getDataForKey().get(key);
        for(int i=0;i<dataInfo.size();i++){
            Integer[] index = dataInfo.get(i);
            lines.add(excelToPojo.getPojo(index[0],index[1]));
        }
        return lines;
    }

}
