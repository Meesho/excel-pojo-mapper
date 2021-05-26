package com.meesho.epmapper;

import com.meesho.epmapper.dataModels.ClassInfo;
import com.meesho.epmapper.utils.ReflectUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import static com.meesho.epmapper.utils.ExcelUtil.*;


public class Generator {

    /**
     * @param rootClassName class name of root pojo
     * @param classSet      set of class names in pojo hirerchy
     * @param list          list of class info
     * @return list of class info
     */
    private static List<ClassInfo> createClassHirerchy(String rootClassName, LinkedHashSet<String> classSet, List<ClassInfo> list) {
        Class<?> root = ReflectUtil.getClassByName(rootClassName);
        Field[] fields = root.getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            String type = field.getType().getName();
            String className = field.getDeclaringClass().getName();
            classSet.add(className);
            if (!type.contains("java.lang") && !type.contains("java.util")) {
                if (type.contains("[L")) {
                    type = type.replace("[L", "").replace(";", "").trim();
                }
                Class<?> child = ReflectUtil.getClassByName(type);
                createClassHirerchy(child.getName(), classSet, list);
            }

            if (type.contains("java.util") && type.contains("List")) {
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                Class<?> listClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                if (listClass.getName().contains("java.lang")) {
                    type = "[L[L" + listClass.getName();
                } else {
                    type = "[L" + listClass.getName();
                    Class<?> child = ReflectUtil.getClassByName(listClass.getName());
                    createClassHirerchy(child.getName(), classSet, list);
                }
            }

            if (field.getType().getName().contains("[L")) {
                list.add(new ClassInfo(name, "[L" + type, className));
            } else {
                list.add(new ClassInfo(name, type, className));
            }

        }
        return list;
    }

    /**
     * @param file      location of excel file
     * @param sheetName name of sheet
     * @param classSet  class name in pojo hirerchy
     * @param map       map of class & field
     */
    private static void createExcelTemplate(String file, String sheetName, LinkedHashSet<String> classSet, Map<String, List<String[]>> map) {
        int cellCount = 1, cellnum = 1;
        FileInputStream fileInputStream = getExcelFileStream(file);
        XSSFSheet sheet = fileInputStream != null ? createSheet(fileInputStream, sheetName) : createSheet(sheetName);
        Row headerRow = sheet.createRow(0);
        Row dataTypeRow = sheet.createRow(1);
        for (String s : classSet) {
            int mergeReq = map.get(s).size();
            int last = cellCount + mergeReq - 1;
            if (cellCount < last)
                sheet.addMergedRegion(new CellRangeAddress(0, 0, cellCount, last));
            cellCount = last + 1;

            Cell cell = headerRow.createCell(cellnum);
            cell.setCellValue(s + ":" + cellnum + ":" + last);

            for (int j = cellnum; j < cellnum + map.get(s).size(); j++) {
                List<String[]> fieldInfo = map.get(s);
                Cell dataTypeRowCell = dataTypeRow.createCell(j);
                dataTypeRowCell.setCellValue(fieldInfo.get(j - cellnum)[0] + ":" + fieldInfo.get(j - cellnum)[1]);
            }
            cellnum = cellCount;
        }
        writeToExcel(file);
    }


    /**
     * Creates excel template
     *
     * @param pojo Class path of pojo
     * @param path excel location & sheet name separated by ":"
     */
    public void generate(String pojo, String path) {
        String[] pathInfo = path.split(":");
        LinkedHashSet<String> classSet = new LinkedHashSet<>();
        List<ClassInfo> classInfos = new ArrayList<>();
        Map<String, List<String[]>> map = new LinkedHashMap<>();
        List<ClassInfo> classHirerchyList = createClassHirerchy(pojo, classSet, classInfos);
        classHirerchyList.forEach(classInfo -> {
            classSet.stream().filter(name -> classInfo.className.equals(name)).forEach(filterdName -> {
                String[] fieldInfo = new String[]{classInfo.fieldName, classInfo.fieldType};
                if (!map.containsKey(filterdName)) {
                    List<String[]> fieldInfoList = new ArrayList<>();
                    fieldInfoList.add(fieldInfo);
                    map.put(filterdName, fieldInfoList);
                } else {
                    List<String[]> previousFieldInfo = map.get(filterdName);
                    previousFieldInfo.add(fieldInfo);
                }
            });
        });
        createExcelTemplate(pathInfo[0], pathInfo[1], classSet, map);
    }
}
