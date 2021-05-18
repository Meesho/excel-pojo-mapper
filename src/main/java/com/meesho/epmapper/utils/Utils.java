package com.meesho.epmapper.utils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {


    public static LinkedHashMap<String, String> sortbykey(Map<String, String> map, Comparator<? super Map.Entry<String, String>> comparator) {
        LinkedHashMap<String, String> temp;
        temp = map.entrySet()
                .stream()
                .sorted(comparator)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        return temp;
    }

    public static <T> List<T> convertArrayToList(T[] array) {
        // create a list from the Array
        return Arrays
                .stream(array)
                .collect(Collectors.toList());
    }

    public static boolean contentIsObjectArray(List<Object> list) {
        if (list.get(0) instanceof Object[]) {
            return true;
        }
        return false;
    }

    public static List<Object[]> getListOfObjectArray(List<Object> list) {
        return list.stream().map(o -> (Object[]) o).collect(Collectors.toList());
    }

    public static List<List<Object>> getListOfList(List<Object> list) {
        return list.stream().map(Utils::castToList).collect(Collectors.toList());
    }

    public static List<Object> castToList(Object object) {
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) object;
        return list;
    }

    public static <T> Object[] convertListToArray(Class<?> componentType, List<Object> list) {
        Object[] array = (Object[]) Array.newInstance(componentType, list.size());
        return list.toArray(array);
    }
}
