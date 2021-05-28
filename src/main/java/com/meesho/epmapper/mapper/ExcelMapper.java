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
    private static final String rootArrayString = "[L" + rootString;
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

    /**
     * @param rootIndex Index of data in list of test data based on class name index of test data
     * @return Object mapped with data
     */
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
                //Iterate for each data in row
                for (int dataCount = 0; dataCount < data.size(); dataCount++) {
                    //Iterate for each cell data
                    for (HashMap<String, Object> map : data.get(dataCount).rowData) {
                        //If field is of type java.lang
                        if (isLangField(fields, fieldName) && map.get(fieldName) != null) {
                            if (size > 1 && langProcessCount < size) {
                                instance = setInitialLangField(instanceClass, map, instance, field, type, fieldName);
                                list.add(instance);
                                langProcessCount++;
                            }
                            Object object = getObjectIfSizeIsExhausted(size, langProcessCount, dataCount, list, instance);
                            set(object, field, map, fieldName, type);
                            //If field is of type pojo object
                        } else if (isRootString(fields, fieldName)) {
                            rootIndex = getIndexOfTestData(fields.get(fieldName));
                            SingletonMap<Integer, List<Object>> tempMap = setInitialRootStringField(size, rootStringProcessCount, rootIndex, dataCount, list, field);
                            rootStringProcessCount = tempMap.getKey();
                            List<Object> unhandled = tempMap.getValue();
                            setRemainingRootStringField(size, rootIndex, dataCount, field, list, unhandled);
                            if (size <= 1) {
                                ReflectUtil.setFieldData(field, instance, mapping(rootIndex));
                            }
                            //If field is of type pojo object array
                        } else if (isRootArrayString(fields, fieldName)) {
                            setRootArrayStringField(fields, fieldName, field, list, instance);
                        }
                    }
                }
            } else if (isRootField(fields, fieldName)) {
                arrList = processRemainingData(fields, field, fieldName, instance);
            }
        }
        return getNonNullObject(list, arrList, instance);
    }


    /**
     * @param instanceClass class name
     * @param map           field & data map
     * @param instance      class instance
     * @param field         Field of class
     * @param type          Custom type based on field is Array Or not.Defined as enum in Helper
     * @param fieldName     Field name of class
     * @return mapped object with data
     */
    private Object setInitialLangField(Class<?> instanceClass, HashMap<String, Object> map, Object instance, Field field, String type, String fieldName) {
        instance = ReflectUtil.newInstanceOf(instanceClass);
        if (isEnd(fieldName, map)) {
            ReflectUtil.setFieldData(field, instance, Helper.Value.valueOf(type).maxValue());
        } else {
            ReflectUtil.setFieldData(field, instance, map.get(fieldName));
        }
        return instance;
    }

    /**
     * @param size                   count of rows filled with data
     * @param rootStringProcessCount index to track iteraration in recursive call
     * @param rootIndex              Index of data in list of test data based on class name
     * @param dataCount              index of data in list of rows
     * @param list                   List of found instances
     * @param field                  Field of class for which value has to be set
     * @return
     */
    private SingletonMap<Integer, List<Object>> setInitialRootStringField(int size, int rootStringProcessCount, int rootIndex, int dataCount, List<Object> list, Field field) {
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
        return new SingletonMap<>(rootStringProcessCount, unhandled);
    }

    /**
     * Map remaining pojo object with data
     *
     * @param size      count of rows filled with data
     * @param rootIndex Index of data in list of test data based on class name index of data in list of test data based on class name
     * @param dataCount index of data in list of rows
     * @param field     Field of class for which value has to be set
     * @param list      List of found instances
     * @param unhandled List of instances not stored in list
     */
    private void setRemainingRootStringField(int size, int rootIndex, int dataCount, Field field, List<Object> list, List<Object> unhandled) {
        if (size > 1) {
            Object mappedObject = mapping(rootIndex);
            if (mappedObject instanceof ArrayList) {
                ReflectUtil.setFieldData(field, list.get(dataCount), Utils.castToList(mappedObject).get(dataCount));
            } else {
                ReflectUtil.setFieldData(field, unhandled.get(dataCount), mappedObject);
            }

        }
    }

    /**
     * Map if pojo object is array
     *
     * @param fields    Map of field name & type
     * @param fieldName Field name of class
     * @param field     Field whose value has to be set
     * @param list      List of found instances
     * @param instance  class instance which has to be set for field
     */
    private void setRootArrayStringField(LinkedHashMap<String, String> fields, String fieldName, Field field, List<Object> list, Object instance) {
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

    /**
     * @param size
     * @param langProcessCount
     * @param dataCount
     * @param list             List of found instances
     * @param instance         class instance which has to be set for field
     * @return
     */
    private Object getObjectIfSizeIsExhausted(int size, int langProcessCount, int dataCount, List<Object> list, Object instance) {
        if (size > 1 && langProcessCount >= size) {
            return list.get(dataCount);
        } else {
            return instance;
        }
    }

    /**
     * @param fields    Map of field name & type
     * @param field     Field of class for which value has to be set
     * @param fieldName Field name of class field
     * @param instance  class instance which has to be set for field
     * @return
     */
    private Object processRemainingData(LinkedHashMap<String, String> fields, Field field, String fieldName, Object instance) {
        int rootIndex = getIndexOfTestData(fields.get(fieldName).replace("[L", ""));
        fieldObjectMap.put(field, instance);
        Object arrList = mapping(rootIndex);
        if (isRootArrayString(fields, fieldName)) {
            if (!(arrList instanceof ArrayList)) {
                objectList.add(arrList);
                if (objectList.size() > 0) {
                    ReflectUtil.setArrayField(field, instance, objectList);
                }
            } else {
                setFieldValue(Utils.castToList(arrList), instance, field);
            }

        } else if (isRootString(fields, fieldName)) {
            ReflectUtil.setFieldData(field, instance, arrList);
            arrList = null;
        }
        return arrList;
    }

    /**
     * @param list     List of found instances
     * @param arrList  List of instance with data mapping
     * @param instance class instance with data mapping
     * @return
     */
    private Object getNonNullObject(List<Object> list, Object arrList, Object instance) {
        if (list.size() > 1) {
            return list;
        } else if ((arrList != null) && instance == null) {
            return arrList;
        } else {
            return instance;
        }
    }

    /**
     * @param obj Object to cast if it is list
     * @return
     */
    private List<Object> getRootList(Object obj) {
        List<Object> rootList = new ArrayList<>();
        try {
            rootList = Utils.castToList(obj);
        } catch (ClassCastException castException) {
            rootList.add(obj);
        }
        return rootList;
    }

    /**
     * Create list based on end indexes
     *
     * @param indexes  List of indexes where content is END
     * @param rootList List of found instances
     * @param field    Field of class for which value has to be set
     * @return
     */
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

    /**
     * Create list based on it's content
     *
     * @param rootSubList Sub list based on end indexes
     * @param tempList    List of sub list
     * @param field       Field of class for which value has to be set
     */
    private void getList(List<Object> rootSubList, List<Object> tempList, Field field) {
        if (!field.getType().getName().contains("List")) {
            tempList.add(Utils.convertListToArray(field.getType().getComponentType(), rootSubList));
        } else {
            tempList.add(rootSubList);
        }
    }

    /**
     * set field based on type
     *
     * @param mappedList list of mapped instances
     * @param list       List of found instances
     * @param field      Field of class for which value has to be set
     */
    private void setFieldValueForList(List<Object> mappedList, List<Object> list, Field field) {
        IntStream.range(0, list.size()).filter(listIndex -> listIndex < mappedList.size()).forEach(index -> {
            if (!Utils.contentIsObjectArray(mappedList)) {
                ReflectUtil.setFieldData(field, list.get(index), new ArrayList<>(Utils.getListOfList(mappedList).get(index)));
            } else {
                ReflectUtil.setFieldData(field, list.get(index), Utils.getListOfObjectArray(mappedList).get(index));
            }
        });
    }


    /**
     * set field based on type, if field type is array or not
     *
     * @param rootList List to set as value of field
     * @param instance Parent instance whose field has to be set
     * @param field    Field of class for which value has to be set
     */
    private void setFieldValue(List<Object> rootList, Object instance, Field field) {
        if (!field.getGenericType().getTypeName().contains("List")) {
            ReflectUtil.setArrayField(field, instance, rootList);
        } else {
            ReflectUtil.setFieldData(field, instance, rootList);
        }
    }

    /**
     * Map two list of instances to get list of mapped instances
     *
     * @param mappedList List of child objects to map with field of parent object
     * @param list       List of parent objects
     * @param field      Field of class for which value has to be set
     */
    private void setFieldValue(List<Object> mappedList, List<Object> list, Field field) {
        IntStream.range(0, list.size()).filter(listIndex -> listIndex < mappedList.size()).forEach(index -> ReflectUtil.setFieldData(field, list.get(index), mappedList.get(index)));
    }


    /**
     * Get list of object based on end index
     *
     * @param indexes  List of indexes where end content present, to find end of data
     * @param rootList List of Objects has object with END content
     * @return
     */
    private List<Object> getObjectListByIndexList(List<Integer> indexes, List<?> rootList) {
        return IntStream.range(0, indexes.size()).mapToObj(mappingIndex -> rootList.get(indexes.get(mappingIndex) - 1)).collect(Collectors.toList());
    }

    /**
     * Get index list for END
     *
     * @param objectList List of Objects has object with END content
     * @return
     */
    private List<Integer> filterEndIndexes(List<?> objectList) {
        return IntStream.range(0, objectList.size()).filter(i -> isEnd(objectList.get(i).toString())).boxed().collect(Collectors.toList());
    }

    /**
     * Check content is END based on data type Long/Integer/Double.Max value in case of Long/Integer/Double will considered END content.
     *
     * @param str string is END or not
     * @return
     */
    private boolean isEnd(String str) {
        return str.contains("END") || str.contains(v1) || str.contains(v2) || str.contains(v3);
    }

    /**
     * Find index of data(class info,column info) based on class name
     *
     * @param str Part of class name
     * @return
     */
    private int getIndexOfTestData(String str) {
        OptionalInt index = IntStream.range(0, testDataList.size()).filter(i -> testDataList.get(i).className.contains(str)).findFirst();
        return index.orElse(-1);
    }

    /**
     * sort field based on field type
     *
     * @param rootIndex Index of data in list of test data based on class name
     * @return
     */
    private LinkedHashMap<String, String> sortTestDataKeys(int rootIndex) {
        Map<String, String> fields_temp = testDataList.get(rootIndex).fields;
        Comparator<Map.Entry<String, String>> comp = new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                if (o1.getValue().contains("java.lang") && o2.getValue().contains(rootString)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        };
        return Utils.sortbykey(fields_temp, comp);
    }

    /**
     * Set field data based on content type
     *
     * @param obj   Parent object whose field to be set
     * @param field Field of parent object
     * @param map   Map of field name & cell content
     * @param name  Field name
     * @param type  Field type
     */
    private void set(Object obj, Field field, HashMap<String, Object> map, String name, String type) {
        try {
            ReflectUtil.setFieldData(field, obj, map.get(name));
        } catch (IllegalArgumentException iae) {
            if (!isEnd(name, map)) {
                Object[] objectArray = (Object[]) map.get(name);
                ReflectUtil.setFieldData(field, obj, Utils.convertArrayToList(objectArray));
            } else if (type.contains("Array")) {
                ReflectUtil.setFieldData(field, obj, Helper.ArrayValue.valueOf(type).maxValue());
            } else {
                ReflectUtil.setFieldData(field, obj, Helper.Value.valueOf(type).maxValue());
            }
        }
    }


    /**
     * Check if content is END
     *
     * @param name Field name
     * @param map  Map of field name & cell content
     * @return
     */
    private boolean isEnd(String name, HashMap<String, Object> map) {
        return map.get(name).equals("END");
    }

    /**
     * @param fields    Map of field name & type
     * @param fieldName Field name of class field
     * @return Field is type of pojo class
     */
    private boolean isRootString(LinkedHashMap<String, String> fields, String fieldName) {
        return fields.get(fieldName).startsWith(rootString);
    }

    /**
     * @param fields    Map of field name & type
     * @param fieldName Field name of class field
     * @return Field is type of array of pojo class
     */
    private boolean isRootArrayString(LinkedHashMap<String, String> fields, String fieldName) {
        return fields.get(fieldName).startsWith(rootArrayString);
    }

    /**
     * @param fields    Map of field name & type
     * @param fieldName Field name of class field
     * @return Field is java.lang field
     */
    private boolean isLangField(LinkedHashMap<String, String> fields, String fieldName) {
        return fields.get(fieldName).contains("java.lang");
    }

    /**
     * @param fields    Map of field name & type
     * @param fieldName Field name of class field
     * @return Field is type of array of pojo class or pojo class
     */
    private boolean isRootField(LinkedHashMap<String, String> fields, String fieldName) {
        return isRootString(fields, fieldName) | isRootArrayString(fields, fieldName);
    }
}
