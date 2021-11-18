package com.meesho.epmapper;

import com.meesho.epmapper.mapper.ExcelToPojo;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ExcelObjectMapperHelper {

    private static @Setter
    ExcelObjectMapper objectMapper;

    /**
     * Fetch data mapped with key
     *
     * @param key Name of data by which data has to be fetched from excel
     * @return
     */
    public static List<Object> getData(String key) {
        if (StringUtils.isNotBlank(objectMapper.getRootPackage()))
            System.setProperty("rootPackage", objectMapper.getRootPackage());
        List<Object> lines = new ArrayList<Object>();
        ExcelToPojo excelToPojo = new ExcelToPojo(objectMapper.getFileLocation(), objectMapper.getSheetName());
        List<Integer[]> dataInfo = excelToPojo.getDataForKey().get(key);
        for (int i = 0; i < dataInfo.size(); i++) {
            Integer[] index = dataInfo.get(i);
            lines.add(excelToPojo.getPojo(index[0], index[1]));
        }
        return lines;
    }

}
