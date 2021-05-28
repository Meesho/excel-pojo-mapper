<h1 align="center">epmapper</h1>
  

*Epmapper is a teeny Java library that provides one way mapping from Excel sheets to Java classes. In a way it lets us convert each row of the specified excel data into Java objects. Epmapper uses Apache Poi (the Java API for Microsoft Documents) under the hood to fulfill the mapping process.Epmapper provides a way to generate excel templates for complex java objects and creating java objects for data kept in excel.*

#### Excel Template Generation:

```
ExcelTemplateGenerator generator = new ExcelTemplateGenerator();
generator.generate(rootPackage,path);
```

- rootPackage: Fully qualified class name of root pojo.
- path: Path of file and sheet name separated with “:”

**Note: Primitive data types are not supported, we can use wrapper classes.**

### Guideline to keep data in excel:

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
