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
            arrStr = "_ARRAY";
        if (string.contains("String")) {
            baseStr = "STRING_VALUE";
        } else if (string.contains("Integer")) {
            baseStr = "INTEGER_VALUE";
        } else if (string.contains("Double")) {
            baseStr = "DOUBLE_VALUE";
        } else if (string.contains("Boolean")) {
            baseStr = "BOOLEAN_VALUE";
        } else if (string.contains("Long")) {
            baseStr = "LONG_VALUE";
        } else {
            baseStr = "END";
        }
        return baseStr + arrStr;
    }


    public enum ArrayValue {
        INTEGER_VALUE_ARRAY {
            public Integer[] value(String[] strings) {
                return Stream.of(strings).mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);
            }

            public List<Integer> maxValue() {
                return Collections.singletonList(Integer.MAX_VALUE);
            }
        }, DOUBLE_VALUE_ARRAY {
            public Double[] value(String[] strings) {
                return Stream.of(strings).mapToDouble(Double::parseDouble).boxed().toArray(Double[]::new);
            }

            public List<Double> maxValue() {
                return Collections.singletonList(Double.MAX_VALUE);
            }
        }, BOOLEAN_VALUE_ARRAY {
            public Boolean[] value(String[] strings) {
                return Stream.of(strings).map(Boolean::parseBoolean).toArray(Boolean[]::new);
            }

            public List<Boolean> maxValue() {
                return Collections.singletonList(Boolean.TRUE);
            }
        }, LONG_VALUE_ARRAY {
            public Long[] value(String[] strings) {
                return Stream.of(strings).mapToLong(Long::parseLong).boxed().toArray(Long[]::new);
            }

            public List<Long> maxValue() {
                return Collections.singletonList(Long.MAX_VALUE);
            }
        }, STRING_VALUE_ARRAY {
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
        INTEGER_VALUE {
            public Integer value(Cell cell) {
                cell.setCellType(CellType.NUMERIC);
                return ((Double) cell.getNumericCellValue()).intValue();
            }

            public Integer maxValue() {
                return Integer.MAX_VALUE;
            }
        }, DOUBLE_VALUE {
            public Double value(Cell cell) {
                cell.setCellType(CellType.NUMERIC);
                return cell.getNumericCellValue();
            }

            public Double maxValue() {
                return Double.MAX_VALUE;
            }
        }, BOOLEAN_VALUE {
            public Boolean value(Cell cell) {
                cell.setCellType(CellType.BOOLEAN);
                return cell.getBooleanCellValue();
            }

            public Boolean maxValue() {
                return Boolean.TRUE;
            }
        }, LONG_VALUE {
            public Long value(Cell cell) {
                cell.setCellType(CellType.NUMERIC);
                return ((Double) cell.getNumericCellValue()).longValue();
            }

            public Long maxValue() {
                return Long.MAX_VALUE;
            }
        }, STRING_VALUE {
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
