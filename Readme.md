
# Mini SQL Query Engine

A lightweight SQL query execution engine implemented in **Java** that simulates how relational databases process queries internally.

This project demonstrates the **complete query processing pipeline** including parsing, validation, and execution using clean **object-oriented design principles**.

---

## Features

Supported SQL operations:

- CREATE TABLE
- INSERT INTO
- SELECT
- WHERE clause with `=` operator
- SELECT specific columns
- SELECT * (all columns)
- UPDATE
- DELETE

Example queries supported:

```sql
CREATE TABLE users (id INT, name STRING, age INT);

INSERT INTO users VALUES (1, "Kartik", 20);

INSERT INTO users VALUES (2, "Rahul", 22);

SELECT * FROM users;

SELECT name FROM users WHERE age = 20;

UPDATE users SET age = 23 WHERE name = "Om";

DELETE FROM users WHERE name = "Yash";
````

---

# Architecture

The engine follows a modular query execution pipeline similar to real database systems.


```
                 SQL Query (User Input)
                        │
                        ▼
                    Main.java
                        │
                        ▼
                   Parser.parse()
            (Tokenization + Query Object)
                        │
                        ▼
                 Query Object (AST)
                        │
                        ▼
            QueryValidator.validate()
             - table existence
             - column validation
             - WHERE validation
                        │
                        ▼
               QueryExecutor.execute()
                        │
                        ▼
                    Database
                        │
                        ▼
                      Table
                        │
                        ▼
                       Row
                        │
                        ▼
                Filter rows (WHERE)
                        │
                        ▼
               Project columns (SELECT)
                        │
                        ▼
                 Print Result (CLI)
```

---

# Detailed Query Execution Flow

Example query:

```sql
SELECT name FROM users WHERE age = 20;
```

Execution steps:

1. **Main** reads the query from the terminal.
2. **Parser** tokenizes the query and builds a `SelectQuery` object.
3. **Validator** checks:

   * table existence
   * column validity
   * WHERE clause correctness
4. **Executor** performs the query:

   * loads the table
   * filters rows using WHERE condition
   * projects requested columns
5. Results are printed to the terminal.

---

# Project Structure

```
mini-sql-query-engine
│
├── main
│   └── Main.java
│
├── parser
│   └── Parser.java
│
├── query
│   ├── Query.java
│   ├── CreateTableQuery.java
│   ├── InsertQuery.java
│   ├── SelectQuery.java
│   ├── UpdateQuery.java
│   ├── DeleteQuery.java
│   └── Condition.java
│
├── validator
│   ├── QueryValidator.java
│   └── ValidationException.java
│
├── executor
│   └── QueryExecutor.java
│
└── storage
    ├── Database.java
    ├── Table.java
    └── Row.java
    └── Column.java
    └── DataType.java
```

---

# Design Concepts Used

This project demonstrates several important backend and system design concepts:

* Object-Oriented Design (OOP)
* Separation of Concerns
* Query Parsing
* Abstract Syntax Tree (Query Objects)
* Validation Layer
* Execution Engine
* Visitor Pattern
* In-memory storage engine

---

#Since your **`Main.java` is in the root (not inside a `main` package)** and you created a **JAR**, the README run instructions should change. Below is a **clean updated section** you can paste into your README.

---

## How to Run

### Clone the repository

```
git clone https://github.com/KartikeyaGupta05/mini-sql-query-engine.git
cd mini-sql-query-engine
```

---

### Option 1: Run using JAR (Recommended)

Download or build the JAR file and run:

```
java -jar mini-sql-engine.jar
```

This will start the SQL engine in the terminal.

Example:

```
Mini SQL Engine Started. Type 'exit' to quit.
SQL>
```

---

### Option 2: Run from Source Code

Compile the project:

```
javac Main.java parser/*.java query/*.java storage/*.java validator/*.java executor/*.java
```

Run the program:

```
java Main
```

---

### Exit the Engine

```
exit
```
---

# Example Session

```
Mini SQL Engine Started. Type 'exit' to quit.

SQL> CREATE TABLE users (id INT,name STRING,age INT);

SQL> INSERT INTO users VALUES (1,"Kartik",20);

SQL> INSERT INTO users VALUES (2,"Rahul",24);

SQL> INSERT INTO users VALUES (3,"Om",22);

SQL> INSERT INTO users VALUES (4,"Yash",22);

SQL> SELECT * FROM users;

id name age
1 Kartikeya 20
2 Rahul 24
3 Om 22
4 Yash 22

SQL> SELECT name FROM users WHERE age = 20;

name
Kartikeya

SQL> UPDATE users SET age = 23 WHERE name = "Om";

SQL> SELECT * FROM users;

id name age
1 Kartikeya 20
2 Rahul 24
3 Om 23
4 Yash 22

SQL> DELETE FROM users WHERE name = "Yash";

SQL> SELECT * FROM users;

id name age
1 Kartikeya 20
2 Rahul 24
3 Om 23
```

---

# Current Limitations

* Data stored only in memory
* Only `=` operator supported in WHERE
* No persistent storage
* No indexing

---

# Future Improvements

Planned upgrades:

* ORDER BY clause
* Disk-based storage
* Indexing for faster queries
* Query optimization

---

# Learning Goals

This project was built to deeply understand:

* How databases process SQL queries internally
* How query parsing and execution pipelines work
* System design using modular architecture
* Clean object-oriented programming in Java

---

# Author

Kartikeya Gupta
Computer Science (AI & DS)
GL Bajaj Group of Institutions