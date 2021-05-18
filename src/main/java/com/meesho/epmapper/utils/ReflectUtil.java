package com.meesho.epmapper.utils;


import com.meesho.epmapper.exceptions.EpmapperInstantiationException;
import com.meesho.epmapper.exceptions.IllegalCastException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

public final class ReflectUtil {

    public static <T> T newInstanceOf(Class<T> type) {
        T obj;
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            obj = constructor.newInstance();
        } catch (Exception ex) {
            throw new EpmapperInstantiationException("Cannot create a new instance of " + type.getName(), ex);
        }
        return obj;
    }

    public static Object newInstanceOf(String className) {
        Object obj = null;
        Class<?> type = null;
        try {
            type = Class.forName(className);
            obj = newInstanceOf(type);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return obj;
    }

    public static Field getField(Class<?> c,String name){
        Field field = null;
        try {
            field = c.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        assert field != null;
        field.setAccessible(true);
        return field;
    }

    public static Class<?> getClassByName(String className){
        Class<?> c = null;
        try {
            c = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return c;
    }

    public static void setFieldData(Field field, Object o, Object value) {
        try {
            field.setAccessible(true);
            field.set(o,value);
        } catch (IllegalAccessException e) {
            throw new IllegalCastException("Unexpected cast type {" + value + "} of field" + field.getName());
        }
    }

    public static void setArrayField(Field field,Object o, List<Object> value){
        Object[] array = Utils.convertListToArray(field.getType().getComponentType(),value);
        try {
            field.setAccessible(true);
            field.set(o,array);
        } catch (IllegalAccessException e) {
            throw new IllegalCastException("Unexpected cast type {" + value + "} of field" + field.getName());
        }
    }
}
