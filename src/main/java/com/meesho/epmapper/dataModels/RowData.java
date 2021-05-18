package com.meesho.epmapper.dataModels;

import java.util.HashMap;
import java.util.List;

public class RowData {
    public List<HashMap<String, Object>> rowData;

    @Override
    public String toString() {
        return "RowData{" +
                "rowData=" + rowData +
                '}';
    }
}
