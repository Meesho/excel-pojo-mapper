<h1 align="center">epmapper</h1>
  
*Epmapper is a tiny java library to map data kept in excel with java objects without writing boilerplate code to read excel & create java objects. In a way it lets us convert rows of the specified excel data into Java objects.Rows can be mapped with a key and can be fetched by key name as list of objects.Library provides way to generate standerd excel template to support mapping of complex hierarchy of objects.Epmapper uses Java Reflection & Apache Poi (the Java API for Microsoft Documents) under the hood to fulfill the mapping process.*


## Requirements

JARs are distributed for Java8.

## Get It

If you are using maven project then create libs folder under project directory.<a id="raw-url" href="https://github.com/Meesho/excel-pojo-mapper/releases/download/v1.0/excel-pojo-mapper-1.0.jar">Download jar</a> & place it in libs folder.Update dependency in pom.xml.


```
<dependency>
  <groupId>com.meesho.epmapper</groupID>
  <artifactId>excel-pojo-mapper</artifactId>
  <scope>system</scope>
  <version>1.0-SNAPSHOT</version>
  <systemPath>${project.basedir}/libs/excel-pojo-mapper-1.0.jar</systemPath>
</dependency>
```

## Getting Started

### Build ExcelObjectMapper instance

ExcelObjectMapper class provides way to set parameters required for excel template generation & mapping data with java objects using builder pattern.

```
ExcelObjectMapper mapper = ExcelObjectMapper.
                           builder().
                           rootPackage(root_package).
                           fileLocation(path_of_excel).
                           sheetName(sheet_name).
                           build();
```
- root_package : Root package in project folder
- path_of_excel : Path of excel file
- sheet_name : Name of excel sheet

### Generate Excel Template:

The Generator class provides a method and it's overloaded version to generate excel template.

1. Create instance of Generator class

```
Generator generator = new Generator();
```
2. Generate excel template

```
generator.generate(pojo,path);
          OR
generator.generate(excelObjectMapper,relativeClassPath);

```

- pojo : Fully qualified class name of root pojo.
- path : Path of excel file and sheet name separated with “:”
- excelObjectMapper : Instance of ExcelObjectMapper class
- relativeClassPath : Class path of root pojo class(excluding root package)

**Note: Primitive data types are not supported, we can use wrapper classes.**

### Fill data in excel:

Generated excel template contains pojo classes & it's fields which is used for mapping data with java objects.Data for each field in class can be filled below field name.If field is a pojo class or array of pojo class, data has to be filled below class below which it's fields are defined.

Following are guideline & examples to fill data in excel template:

1. The 1st Column in template is blank to keep key to fetch specific data from template. 
2. Don’t remove auto generated header in template as it is used for mapping data with java objects.
3. Data will be filled in rows from the 2nd column.
4. Range of columns is specified for each class based on defined fields.Fields are present in the header of template, data has to be kept in respective columns.
5. Each data in excel will be separated by an empty row.

### Examples:

#### Object Model

```
public class Student {
   private String name;
   private Integer rollNumber;
   private Integer age;
}
```
#### Data in excel template:

&nbsp;|Student:1:3|&nbsp;|&nbsp;
------------|-------------|--------------|----------------
&nbsp;|name:java.lang.String|rollNumber:java.lang.Integer|age:java.lang.Integer
key1|Rahul|1|25
&nbsp;|&nbsp;|&nbsp;|&nbsp;
key2|Raj|3|30

#### Object Model

```
public class Employee {

       private Long employeeId;

       private String name;

       private Boolean single;

       List<String> emails;

       List<Integer> bills;
}
```
#### Data in excel template:

&nbsp;|Employee:1:8|&nbsp;|&nbsp;|&nbsp;|&nbsp;|&nbsp;
------------| -------------|--------------|----------------|-----------|---------------|-----------------
&nbsp;|employeeId:java.lang.Long|name:java.lang.String|single:java.lang.Boolean|emails:[L[Ljava.lang.String|bills:[L[Ljava.lang.Integer
key1|1001|Rahul|TRUE|rahul@gmail.com,abc@gmail.com|112,113
&nbsp;|&nbsp;|&nbsp;|&nbsp;|&nbsp;|&nbsp;
key2|1002|Raj|FALSE|raj@gmail.com,kumar@gmail.com|116,117

#### Object Model

```
public class Student {
    private String name;
    private Integer rollNumber;
    private StudentAddress address;
}

public class StudentAddress {
    private String city;
    private String state;
    private Long pinCode;
}

```
#### Data in excel template:

&nbsp;|Employee:1:8|&nbsp;|StudentAddress:5:7|&nbsp;|&nbsp;|&nbsp;
------|------------|------|------------------|------|------|-------
&nbsp;|name:java.lang.String|rollNumber:java.lang.Integer|StudentAddress|city:java.lang.String|state:java.lang.String|pinCode:java.lang.Long
key1|Rahul|1|&nbsp;|Bangalore|Karnataka|560061
&nbsp;|&nbsp;|&nbsp;|&nbsp;|&nbsp;|&nbsp;|&nbsp;
key2|Raj|2|&nbsp;|Bangalore|Karnataka|560063

#### Object Model

```
public class Department {
    private String name;
    private List<Employee> employees;
}

public class Employee {

       private Long employeeId;

       private String name;
}

```

#### Data in excel template:

&nbsp;|Department:1:2|&nbsp;|Employee:3:4|&nbsp;
------|------------|------|------------------|------
&nbsp;|name:java.lang.String|employees:[LEmployee|employeeId:java.lang.Long|name:java.lang.String
key1|IT|&nbsp;|110|Rahul
&nbsp;|&nbsp;|&nbsp;|111|Raj
&nbsp;|&nbsp;|&nbsp;|&nbsp;|&nbsp;
key2|HR|&nbsp;|112|Amar
&nbsp;|&nbsp;|&nbsp;|113|Prakash

#### Object Model

```
public class Department {
    private String name;
    private Team team;
}

public class Team {
    private String name;
    private List<Employee> employees;
}

public class Employee {

        private Long employeeId;

        private String name;
}
```

#### Data in excel template:

&nbsp;|Department:1:2|&nbsp;|Team:3:4|&nbsp;|Employee:5:6|&nbsp;
------|------------|------|------------------|-----------|------|-----
&nbsp;|name:java.lang.String|team:Team|name:java.lang.String|employees:[LEmployee|employeeId:java.lang.Long|name:java.lang.String
key1|IT|&nbsp;|Dev|&nbsp;|110|Rahul
&nbsp;|&nbsp;|&nbsp;|&nbsp;|&nbsp;|111|Raj
&nbsp;|&nbsp;|&nbsp;|&nbsp;|&nbsp;|&nbsp;|&nbsp;
key2|HR|&nbsp;|Recruitment|&nbsp;|114|Suraj
&nbsp;|&nbsp;|&nbsp;|&nbsp;|&nbsp;|115|Ram

#### Object Model

```
public class Department {
    private String name;
    private List<Team> teams;
}

public class Team {
    private String name;
    private List<Employee> employees;
}

public class Employee {

        private Long employeeId;

        private String name;

        @Override
        public String toString() {
                final StringBuilder sb = new StringBuilder("Employee{");
                sb.append("employeeId=").append(employeeId);
                sb.append(", name='").append(name).append('\'');
                sb.append('}');
                return sb.toString();
        }
}
```
#### Data in excel template:
&nbsp;|Department:1:2|&nbsp;|Team:3:4|&nbsp;|Employee:5:6|&nbsp;
------|------------|------|------------------|-----------|------|-----
&nbsp;|name:java.lang.String|teams:[LTeam|name:java.lang.String|employees:[LEmployee|employeeId:java.lang.Long|name:java.lang.String
key1|IT|&nbsp;|Dev|&nbsp;|110|Rahul
&nbsp;|&nbsp;|&nbsp;|QA|&nbsp;|111|Raj
&nbsp;|&nbsp;|&nbsp;|&nbsp;|&nbsp;|END|END
&nbsp;|&nbsp;|&nbsp;|&nbsp;|&nbsp;|112|Amar
&nbsp;|&nbsp;|&nbsp;|&nbsp;|&nbsp;|113|Prakash
&nbsp;|&nbsp;|&nbsp;|&nbsp;|&nbsp;|&nbsp;|&nbsp;
key2|HR|&nbsp;|Recruitment|&nbsp;|114|Suraj
&nbsp;|&nbsp;|&nbsp;|Support|&nbsp;|115|Ram
&nbsp;|&nbsp;|&nbsp;|&nbsp;|&nbsp;|END|END
&nbsp;|&nbsp;|&nbsp;|&nbsp;|&nbsp;|116|Shyam
&nbsp;|&nbsp;|&nbsp;|&nbsp;|&nbsp;|117|Akash

## Map data with list of java objects:

ExcelObjectMapperHelper class provides methods to pass instance of ExcelObjectMapper and fetch list of java objects by key.

```
ExcelObjectMapperHelper.setObjectMapper(excelObjectMapper);
List<Object> data = ExcelObjectMapperHelper.getData(key);

```

- excelObjectMapper : Instance of ExcelObjectMapper
- key : Name or key mapped with rows of data
- data : List of java objects corresponding to rows in excel

## Write to us:

In case of any suggestion/feedback/issues, send email to ashish.k@meesho.com.

