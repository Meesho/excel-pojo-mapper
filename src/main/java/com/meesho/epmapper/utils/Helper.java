package com.meesho.epmapper.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class Helper {


    public static String fieldType(String string) {
        String arrStr = "";
        String baseStr = "";
        if (string.contains("[L[L"))
            arrStr = "Array";
        if (string.contains("String")) {
            baseStr = "StringValue";
        } else if (string.contains("Integer")) {
            baseStr = "IntegerValue";
        } else if (string.contains("Double")) {
            baseStr = "DoubleValue";
        } else if (string.contains("Boolean")) {
            baseStr = "BooleanValue";
        } else if (string.contains("Long")) {
            baseStr = "LongValue";
        } else {
            baseStr = "END";
        }
        return baseStr + arrStr;
    }


    public enum ArrayValue {
        IntegerValueArray {
            public Integer[] value(String[] strings) {
                return Stream.of(strings).mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);
            }

            public List<Integer> maxValue() {
                return Collections.singletonList(Integer.MAX_VALUE);
            }
        }, DoubleValueArray {
            public Double[] value(String[] strings) {
                return Stream.of(strings).mapToDouble(Double::parseDouble).boxed().toArray(Double[]::new);
            }

            public List<Double> maxValue() {
                return Collections.singletonList(Double.MAX_VALUE);
            }
        }, BooleanValueArray {
            public Boolean[] value(String[] strings) {
                return Stream.of(strings).map(Boolean::parseBoolean).toArray(Boolean[]::new);
            }

            public List<Boolean> maxValue() {
                return Collections.singletonList(Boolean.TRUE);
            }
        }, LongValueArray {
            public Long[] value(String[] strings) {
                return Stream.of(strings).mapToLong(Long::parseLong).boxed().toArray(Long[]::new);
            }

            public List<Long> maxValue() {
                return Collections.singletonList(Long.MAX_VALUE);
            }
        }, StringValueArray {
            public String[] value(String[] strings) {
                return strings;
            }

            public List<String> maxValue() {
                return Collections.singletonList("END");
            }
        };

        public abstract <T> T[] value(String[] strings);

        public abstract <T> List<T> maxValue();
    }

    public enum Value {
        IntegerValue {
            public Integer value(Cell cell) {
                cell.setCellType(CellType.NUMERIC);
                return ((Double) cell.getNumericCellValue()).intValue();
            }

            public Integer maxValue() {
                return Integer.MAX_VALUE;
            }
        }, DoubleValue {
            public Double value(Cell cell) {
                cell.setCellType(CellType.NUMERIC);
                return cell.getNumericCellValue();
            }

            public Double maxValue() {
                return Double.MAX_VALUE;
            }
        }, BooleanValue {
            public Boolean value(Cell cell) {
                cell.setCellType(CellType.BOOLEAN);
                return cell.getBooleanCellValue();
            }

            public Boolean maxValue() {
                return Boolean.TRUE;
            }
        }, LongValue {
            public Long value(Cell cell) {
                cell.setCellType(CellType.NUMERIC);
                return ((Double) cell.getNumericCellValue()).longValue();
            }

            public Long maxValue() {
                return Long.MAX_VALUE;
            }
        }, StringValue {
            public String value(Cell cell) {
                cell.setCellType(CellType.STRING);
                return cell.getStringCellValue();
            }

            public String maxValue() {
                return "END";
            }
        }, END {
            public String value(Cell cell) {
                return "END";
            }

            public String maxValue() {
                return "END";
            }
        };

        public abstract <T> T value(Cell cell);

        public abstract <T> T maxValue();
    }

}
