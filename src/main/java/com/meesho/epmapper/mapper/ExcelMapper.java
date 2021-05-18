package com.meesho.epmapper.mapper;


import com.meesho.epmapper.dataModels.RowData;
import com.meesho.epmapper.dataModels.TestData;
import com.meesho.epmapper.utils.Helper;
import com.meesho.epmapper.utils.ReflectUtil;
import com.meesho.epmapper.utils.Utils;
import org.apache.commons.collections4.map.SingletonMap;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExcelMapper {
    private static final String v1 = String.valueOf(Integer.MAX_VALUE);
    private static final String v2 = String.valueOf(Long.MAX_VALUE);
    private static final String v3 = String.valueOf(Double.MAX_VALUE);
    private static final String rootString = System.getProperty("rootPackage");
    private static final String rootArrayString = "[L"+rootString;
    private List<TestData> testDataList;
    private List<List<RowData>> dataList;
    private List<Object> objectList;
    private HashMap<Field, Object> fieldObjectMap;


    public void setMapperData(List<TestData> testDataList, List<List<RowData>> dataList, List<Object> objectList, HashMap<Field, Object> fieldObjectMap) {
        this.testDataList = testDataList;
        this.dataList = dataList;
        this.objectList = objectList;
        this.fieldObjectMap = fieldObjectMap;
    }

    public Object mapping(int rootIndex) {
        List<Object> list = new ArrayList<>();
        Object arrList = null;
        int langProcessCount = 0, rootStringProcessCount = 0;
        String className = testDataList.get(rootIndex).className.replace("[L", "");
        Object instance = ReflectUtil.newInstanceOf(className);
        Class<?> instanceClass = ReflectUtil.getClassByName(className);
        LinkedHashMap<String, String> fields = sortTestDataKeys(rootIndex);
        for (String fieldName : fields.keySet()) {
            Field field = ReflectUtil.getField(instanceClass, fieldName);
            rootIndex = getIndexOfTestData(instanceClass.getName());
            List<RowData> data = dataList.get(rootIndex);
            String type = Helper.fieldType(fields.get(fieldName));
            int size = data.size();
            if (size > 0) {
                for (int dataCount = 0; dataCount < data.size(); dataCount++) {
                    for (HashMap<String, Object> map : data.get(dataCount).rowData) {
                        if (fields.get(fieldName).contains("java.lang") && map.get(fieldName) != null) {
                            if (size > 1 && langProcessCount < size) {
                                instance = setInitialLangField(instanceClass, map, instance, field, type, fieldName);
                                list.add(instance);
                                langProcessCount++;
                            }
                            Object object = getObjectIfSizeIsExhausted(size,langProcessCount,dataCount,list,instance);
                            set(object,field, map, fieldName, type);
                        } else if (fields.get(fieldName).startsWith(rootString)) {
                            rootIndex = getIndexOfTestData(fields.get(fieldName));
                            SingletonMap<Integer,List<Object>> tempMap = setInitialRootStringField(size,rootStringProcessCount,rootIndex,dataCount,list,field);
                            rootStringProcessCount = tempMap.getKey();
                            List<Object> unhandled = tempMap.getValue();
                            setRemainingRootStringField(size,rootIndex,dataCount,field,list,unhandled);
                            if(size<=1) {
                                ReflectUtil.setFieldData(field, instance, mapping(rootIndex));
                            }
                        } else if (fields.get(fieldName).startsWith(rootArrayString)) {
                            setRootArrayStringField(fields,fieldName,field,list,instance);
                        }
                    }
                }
            } else {
                arrList = processRemainingData(fields,field,fieldName,instance);
            }
        }
        return getNonNullObject(list, arrList, instance);
    }


    private Object setInitialLangField(Class<?> instanceClass,HashMap<String, Object> map,Object instance,Field field,String type,String fieldName){
        instance = ReflectUtil.newInstanceOf(instanceClass);
        if (isEnd(fieldName,map)) {
            ReflectUtil.setFieldData(field, instance, Helper.Value.valueOf(type).maxValue());
        } else {
            ReflectUtil.setFieldData(field, instance, map.get(fieldName));
        }
        return instance;
    }

    private SingletonMap<Integer,List<Object>> setInitialRootStringField(int size,int rootStringProcessCount,int rootIndex,int dataCount,List<Object> list,Field field){
        List<Object> unhandled = new ArrayList<>();
        if (size > 1 && rootStringProcessCount < size) {
            List<?> rootList = (List<?>) mapping(rootIndex);
            List<Integer> indexes = filterEndIndexes(rootList);
            if (indexes.size() > 0) {
                indexes.add(rootList.size());
                List<Object> mappedList = getObjectListByIndexList(indexes, rootList);
                if (list.size() > 1)
                    setFieldValue(mappedList, list, field);
            } else {
                ReflectUtil.setFieldData(field, list.get(dataCount), rootList.get(dataCount));
                unhandled.add(mapping(rootIndex));
                rootStringProcessCount++;
            }
        }
        return new SingletonMap<>(rootStringProcessCount,unhandled);
    }

    private void setRemainingRootStringField(int size,int rootIndex,int dataCount,Field field,List<Object> list,List<Object> unhandled){
        if (size > 1) {
            Object mappedObject = mapping(rootIndex);
            if (mappedObject instanceof ArrayList) {
                ReflectUtil.setFieldData(field, list.get(dataCount), Utils.castToList(mappedObject).get(dataCount));
            } else {
                ReflectUtil.setFieldData(field, unhandled.get(dataCount), mappedObject);
            }

        }
    }

    private void setRootArrayStringField(LinkedHashMap<String, String> fields,String fieldName,Field field,List<Object> list,Object instance){
        List<Object> mappedList = new ArrayList<>();
        String clName = fields.get(fieldName).replace("[L", "");
        int rootIndex = getIndexOfTestData(clName);
        Object obj = mapping(rootIndex);
        List<Object> rootList = getRootList(obj);
        List<Integer> indexes = filterEndIndexes(rootList);

        if (indexes.size() > 0) {
            indexes.add(rootList.size());
            mappedList = getObjectListByIndexList(indexes, rootList, field);
        }

        if (list.size() > 1 && mappedList.size() > 1) {
            setFieldValueForList(mappedList, list, field);

        } else {
            setFieldValue(rootList, instance, field);
        }
    }

    private Object getObjectIfSizeIsExhausted(int size,int langProcessCount,int dataCount,List<Object> list,Object instance){
        if (size > 1 && langProcessCount >= size) {
            return list.get(dataCount);
        } else {
            return instance;
        }
    }

    private Object processRemainingData(LinkedHashMap<String, String> fields,Field field,String fieldName,Object instance){
        int rootIndex = getIndexOfTestData(fields.get(fieldName).replace("[L", ""));
        fieldObjectMap.put(field, instance);
        Object arrList = mapping(rootIndex);
        if (fields.get(fieldName).startsWith("[Lcom.edmapper")) {
            if (!(arrList instanceof ArrayList)) {
                objectList.add(arrList);
                if (objectList.size() > 0) {
                    ReflectUtil.setArrayField(field,instance,objectList);
                }
            } else {
                setFieldValue(Utils.castToList(arrList), instance, field);
            }

        } else if (fields.get(fieldName).startsWith("com.edmapper")) {
            ReflectUtil.setFieldData(field, instance, arrList);
            arrList = null;
        }
        return arrList;
    }

    private Object getNonNullObject(List<Object> list, Object arrList, Object instance) {
        if (list.size() > 1) {
            return list;
        } else if ((arrList != null) && instance == null) {
            return arrList;
        } else {
            return instance;
        }
    }

    private List<Object> getRootList(Object obj) {
        List<Object> rootList = new ArrayList<>();
        try {
            rootList = Utils.castToList(obj);
        } catch (ClassCastException castException) {
            rootList.add(obj);
        }
        return rootList;
    }

    private List<Object> getObjectListByIndexList(List<Integer> indexes, List<Object> rootList, Field field) {
        List<Object> tempList = new ArrayList<>();
        int jump = 0;
        for (Integer index : indexes) {
            List<Object> rootSubList = rootList.subList(jump, index);
            jump = index + 1;
            getList(rootSubList, tempList, field);
        }
        return tempList;
    }

    private void getList(List<Object> rootSubList, List<Object> tempList, Field field) {
        if (!field.getType().getName().contains("List")) {
            tempList.add(Utils.convertListToArray(field.getType().getComponentType(),rootSubList));
        } else {
            tempList.add(rootSubList);
        }
    }

    private void setFieldValueForList(List<Object> mappedList, List<Object> list, Field field) {
        IntStream.range(0,list.size()).filter(listIndex -> listIndex< mappedList.size()).forEach(index -> {
            if (!Utils.contentIsObjectArray(mappedList)) {
                ReflectUtil.setFieldData(field, list.get(index), new ArrayList<>(Utils.getListOfList(mappedList).get(index)));
            } else {
                ReflectUtil.setFieldData(field, list.get(index), Utils.getListOfObjectArray(mappedList).get(index));
            }
        });
    }

    private void setFieldValue(List<Object> rootList, Object instance, Field field) {
        if (!field.getGenericType().getTypeName().contains("List")) {
            ReflectUtil.setArrayField(field,instance,rootList);
        } else {
            ReflectUtil.setFieldData(field, instance, rootList);
        }
    }

    private void setFieldValue(List<Object> mappedList, List<Object> list, Field field) {
        IntStream.range(0,list.size()).filter(listIndex -> listIndex< mappedList.size()).forEach(index -> ReflectUtil.setFieldData(field, list.get(index), mappedList.get(index)));
    }

    private List<Object> getObjectListByIndexList(List<Integer> indexes, List<?> rootList) {
        return IntStream.range(0,indexes.size()).mapToObj(mappingIndex -> rootList.get(indexes.get(mappingIndex) - 1)).collect(Collectors.toList());
    }

    private List<Integer> filterEndIndexes(List<?> objectList) {
        return IntStream.range(0,objectList.size()).filter(i -> isEnd(objectList.get(i).toString())).boxed().collect(Collectors.toList());
    }

    private boolean isEnd(String str) {
        return str.contains("END") || str.contains(v1) || str.contains(v2) || str.contains(v3);
    }

    private int getIndexOfTestData(String str) {
        OptionalInt index = IntStream.range(0,testDataList.size()).filter(i -> testDataList.get(i).className.contains(str)).findFirst();
        return index.orElse(-1);
    }

    private LinkedHashMap<String, String> sortTestDataKeys(int rootIndex) {
        Map<String, String> fields_temp = testDataList.get(rootIndex).fields;
        Comparator<Map.Entry<String, String>> comp = new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                if (o1.getValue().contains("java.lang") && o2.getValue().contains("com.edmapper")) {
                    return -1;
                } else {
                    return 1;
                }
            }
        };
        return Utils.sortbykey(fields_temp, comp);
    }

    private void set(Object obj, Field field, HashMap<String, Object> map, String name, String type) {
        try {
            ReflectUtil.setFieldData(field, obj, map.get(name));
        } catch (IllegalArgumentException iae) {
            if (!isEnd(name, map)) {
                Object[] objectArray = (Object[]) map.get(name);
                ReflectUtil.setFieldData(field, obj, Utils.convertArrayToList(objectArray));
            } else {
                ReflectUtil.setFieldData(field, obj, Helper.Value.valueOf(type).maxValue());
            }
        }
    }


    private boolean isEnd(String name, HashMap<String, Object> map) {
        return map.get(name).equals("END");
    }
}
