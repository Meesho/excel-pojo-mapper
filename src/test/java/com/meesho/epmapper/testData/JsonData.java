package com.meesho.epmapper.testData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public enum JsonData {
    NestedArrayJson("NestedArray.json"), BasketJson("Basket.json"), ProductionJson("Production.json");

    private String fileName;
    private Type DATA_TYPE = new TypeToken<List<Map<String, Object>>>() {
    }.getType();

    JsonData(String fileName) {
        this.fileName = fileName;
    }

    public List<Object> getDataForKey(String key) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(fileName);
        Reader reader = new InputStreamReader(is);
        List<Map<String, Object>> dataModels = readData(reader);
        for (Map<String, Object> model : dataModels) {
            if (model.get(key) != null) {
                return (List<Object>) model.get(key);
            }
        }
        return null;
    }

    private Object getData(Map<String, Object> dataModel, String key) {
        return dataModel.get(key);
    }

    private List<Map<String, Object>> readData(Reader reader) {
        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(reader);
        return gson.fromJson(reader, DATA_TYPE);
    }
}
