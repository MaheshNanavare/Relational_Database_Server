# Lightweight SQL Database Engine

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

### Workflow
1. The user sends a command to the server.
2. The **tokenizer** breaks the input into structured tokens.
3. The **parser** processes the tokens and constructs a command object.
4. The **DBServer** executes the command, modifying database state or retrieving data.
5. Results (or error messages) are returned to the user.

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

## Testing
The `ComprehensiveTest.java` file includes 20+ rigorous test cases covering:
- Basic operations (`CREATE`, `INSERT`, `SELECT`, `DELETE`).
- Condition parsing (`WHERE` with `AND`, `OR`, `LIKE`).
- Error handling for incorrect queries.
- Persistence testing to ensure data is retained after server restart.
- Edge cases such as invalid column names and malformed `JOIN` statements.

## Future Enhancements
- **Transaction Support**: Implement rollback and commit functionality.
- **Indexing**: Optimize query performance.
- **Concurrency Control**: Enable multi-user support with locks.
