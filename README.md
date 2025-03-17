# Relational Database Server  

## Overview  
This project is a lightweight relational database server that processes SQL-like queries and operates on a file-based storage system. It supports essential database operations like creating tables, inserting records, updating values, deleting entries, and performing joins.  

## Features  
- Supports core SQL commands: `CREATE`, `INSERT`, `SELECT`, `UPDATE` and `DELETE`.  
- Implements a custom query parser and condition evaluator.  
- Ensures data persistence across server restarts.  
- Supports complex queries using `WHERE` and `JOIN` conditions.  
- Provides robust error handling for invalid commands and constraints.

## Installation  
1. Clone the repository:  
   ```sh
   git clone https://github.com/MaheshNanavare/Relational_Database_Server
   cd your-repo-name
   ```  
2. Compile the project:  
   ```sh
   javac -d bin -sourcepath src src/edu/uob/DBServer.java
   ```  
3. Run the server:  
   ```sh
   java -cp bin edu.uob.DBServer
   ```  

## Usage  
### **Basic SQL Commands**  
- Create a database and use it:  
  ```sql
  CREATE DATABASE university;
  USE university;
  ```  
- Create a table:  
  ```sql
  CREATE TABLE students (name, age);
  ```  
- Insert data:  
  ```sql
  INSERT INTO students VALUES ('Mahesh', 22);
  ```  
- Select data:  
  ```sql
  SELECT * FROM students;
  SELECT name FROM students WHERE age > 20;
  ```  
- Update data:  
  ```sql
  UPDATE students SET age = 23 WHERE name == 'Mahesh';
  ```  
- Delete data:  
  ```sql
  DELETE FROM students WHERE name == 'Mahesh';
  ```  
- Join tables:  
  ```sql
  CREATE TABLE grades (student_name, grade);
  INSERT INTO grades VALUES ('Mahesh', 'A');
  JOIN students AND grades ON name AND student_name;
  ```  

## Testing  
Run unit tests using JUnit:  
```sh
javac -cp ".:junit-5.7.0.jar" -d bin -sourcepath src src/edu/uob/ExampleDBTests.java
java -cp ".:bin:junit-5.7.0.jar" org.junit.runner.JUnitCore edu.uob.ExampleDBTests
```  
