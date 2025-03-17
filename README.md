# Lightweight SQL Database Server

## Overview
This project implements a lightweight database management system (DBMS) that interprets a custom SQL-like language. It supports fundamental database operations such as creating databases and tables, inserting and updating records, performing queries with `WHERE` conditions, and executing `JOIN` operations.

## Features
- **Custom SQL-like Syntax**: Supports commands such as `CREATE`, `INSERT`, `SELECT`, `UPDATE`, `DELETE`, `JOIN`, and `ALTER`.
- **Condition Parsing**: Implements a condition parser to handle complex queries with `AND`, `OR`, `LIKE`, and comparison operators.
- **Persistent Storage**: Data is stored in tab-separated files (`.tab`) and reloaded upon server restart.
- **Error Handling**: Provides clear error messages for invalid queries and incorrect syntax.
- **Recursive Descent Parsing**: Uses a tokenizer and parser to process commands according to the project's BNF grammar.
- **Lightweight Design**: Focuses on essential DBMS operations rather than being a full-fledged SQL database.

## System Design

### Architecture
- **Tokenizer**: Breaks input queries into structured tokens.
- **Parser**: Analyzes tokens according to BNF grammar and constructs command objects.
- **DBServer**: Acts as the central processing unit, handling command execution and database state.
- **Commands (`DBcmd`)**: Abstract base class for all database operations, extended by classes such as `SelectCMD`, `InsertCMD`, and `DeleteCMD`.
- **Storage Layer**: Data is persisted in `.tab` files under a directory-based storage system.

### Data Structure
- **DBServer**: Contains a list of `Database` objects, managing overall database state.
- **Database**: Maintains a hashmap of `Table` objects, where each table is uniquely identified.
- **Table**: Stores both metadata and data:
  - A list of column names (Strings) defining the schema.
  - A list of `Row` objects containing actual data.
- **Row**: Represents a single entry in a table, storing values as a list of Strings.

## Source Code Structure
The project follows a structured package organization for better maintainability:

### `edu.uob.commands`
Contains classes that represent different SQL operations:
- `DeleteCMD.java`
- `DropCMD.java`
- `InsertCMD.java`
- `JoinCMD.java`
- `SelectCMD.java`
- `UpdateCMD.java`
- `UseCMD.java`

### `edu.uob.dataclasses`
Defines the core database structures:
- `Database.java`: Represents a database and its tables.
- `Row.java`: Represents a single row in a table.
- `Table.java`: Represents a table in the database.

### `edu.uob.parsers`
Contains classes for parsing conditions and statements:
- `CompoundCondition.java`
- `Condition.java`
- `ConditionParser.java`
- `NameValuePair.java`
- `SetParser.java`
- `SimpleCondition.java`

### `edu.uob.supporters`
Provides essential support functionalities:
- `DataLoader.java`: Handles data loading and saving.
- `DBcmd.java`: Abstract base class for all commands.
- `Parser.java`: General parser for SQL statements.
- `Tokeniser.java`: Tokenizes input SQL commands.

### Main Server and Client
- `DBClient.java`: Implements a client to interact with the database.
- `DBServer.java`: The core database server handling user requests.

### Testing
- `BasicTests.java`
- `ExampleDBTests.java`

## Supported SQL Commands

### Database & Table Management
```sql
CREATE DATABASE university;
USE university;
CREATE TABLE students (name, age);
DROP TABLE students;
DROP DATABASE university;
```

### Data Manipulation
```sql
INSERT INTO students VALUES ('Mahesh', 22);
SELECT * FROM students WHERE age > 21;
UPDATE students SET age = 24 WHERE name == 'Aya';
DELETE FROM students WHERE name == 'Shrirang';
```

### Advanced Queries
```sql
SELECT name, age FROM students WHERE age >= 20 AND age <= 25;
JOIN students AND grades ON name AND student_name;
```

## Edge Case Handling
- Prevents ID column from being explicitly created by the user.
- Ensures JOIN queries do not include the joined column in the result.
- Detects invalid operations, such as inserting incorrect column counts or using incorrect condition syntax.

## Future Enhancements
- **Transaction Support**: Implement rollback and commit functionality.
- **Indexing**: Optimize query performance.
- **Concurrency Control**: Enable multi-user support with locks.
