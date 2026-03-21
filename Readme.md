# Mini SQL Query Engine

A lightweight **SQL query execution engine built in Java** that simulates how relational databases process queries internally.

This project demonstrates the **complete query lifecycle**:

```
Parsing → Validation → Execution → Storage → Optimization (Indexing)
```

It is designed to strengthen understanding of **DBMS internals, OOP, and system design concepts**.

---

## 🚀 Features

### Supported SQL Operations

* CREATE TABLE (with data types)
* INSERT INTO
* SELECT

  * Specific columns
  * SELECT *
  * WHERE clause
  * AND / OR conditions
  * Comparison operators (`=`, `<`, `>`)
  * ORDER BY (ASC / DESC)
* UPDATE
* DELETE

---

### ⚡ Advanced Features

* File-based persistence (data stored on disk)
* Indexing for fast query execution
* Index maintenance (INSERT / UPDATE / DELETE)
* Compound conditions (AND / OR)
* Sorting support (ORDER BY)

---

## 🧪 Example Queries

```sql
CREATE TABLE users (id INT, name STRING, age INT);

INSERT INTO users VALUES (1, "Kartikeya", 20);
INSERT INTO users VALUES (2, "Rahul", 24);
INSERT INTO users VALUES (3, "Om", 22);

SELECT * FROM users;

SELECT name FROM users WHERE age = 20;

UPDATE users SET age = 23 WHERE name = "Om";

DELETE FROM users WHERE name = "Rahul";

SELECT * FROM users WHERE age > 20 AND age < 25;

SELECT * FROM users WHERE age = 22 ORDER BY name DESC;
```

---

## 🏗️ Architecture

The engine follows a modular pipeline similar to real database systems:

```
                 SQL Query (User Input)
                        │
                        ▼
                    Main.java
                        │
                        ▼
                   Parser.parse()
                        │
                        ▼
                 Query Object (AST)
                        │
                        ▼
            QueryValidator.validate()
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
                Filter (WHERE)
                        │
                        ▼
                Sort (ORDER BY)
                        │
                        ▼
               Project (SELECT)
                        │
                        ▼
                 Print Result
```

---

## ⚙️ Query Execution Flow

Example:

```sql
SELECT name FROM users WHERE age = 20;
```

Execution:

1. **Parser**

   * Tokenizes query
   * Builds `SelectQuery` object

2. **Validator**

   * Checks table existence
   * Validates columns
   * Validates WHERE clause

3. **Executor**

   * Fetches table
   * Uses index (if available)
   * Filters rows
   * Sorts data
   * Projects selected columns

4. **Output**

   * Printed to terminal

---

## 📁 Project Structure

```
mini-sql-query-engine
│
├── Main.java
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
├── storage
│   ├── Database.java
│   ├── Table.java
│   ├── Row.java
│   ├── Column.java
│   └── DataType.java
│
└── data
    └── *.table (persisted data)
```

---

## 🧠 Indexing (Optimization)

* HashMap-based indexing:

  ```
  Map<Column, Map<Value, List<Row>>>
  ```
* Used for fast lookup in:

  * SELECT
  * UPDATE
  * DELETE

### Performance Impact

| Query Type | Without Index | With Index |
| ---------- | ------------- | ---------- |
| WHERE =    | O(n)          | O(1)       |
| UPDATE     | O(n)          | O(k)       |
| DELETE     | O(n)          | O(k)       |

---

## ▶️ How to Run

### Clone Repository

```
git clone https://github.com/KartikeyaGupta05/mini-sql-query-engine.git
cd mini-sql-query-engine
```

---

### 🔹 Run from Source (Recommended)

Compile:

```
javac Main.java parser/*.java query/*.java storage/*.java validator/*.java executor/*.java
```

Run:

```
java Main
```

---

### 🔹 Run using JAR

### Build JAR file (if not already built)
```
javac Main.java parser/*.java query/*.java storage/*.java validator/*.java executor/*.java
```

```
jar cfe mini-sql-engine.jar Main *.class parser/*.class query/*.class storage/*.class validator/*.class executor/*.class
```

### Run JAR file
```
java -jar mini-sql-engine.jar
```

---

### Exit

```
exit
```

---

## 💻 Example Session

```
SQL> CREATE TABLE users (id INT,name STRING,age INT);

SQL> INSERT INTO users VALUES (1,"Kartikeya",20);

SQL> SELECT * FROM users;

id name age
1 Kartikeya 20

SQL> SELECT name FROM users WHERE age = 20;

name
Kartikeya
```

---

## ⚠️ Limitations

* No JOIN support
* No GROUP BY / Aggregation
* No query optimizer (cost-based)
* Limited SQL grammar

---

## 🚀 Future Improvements

* GROUP BY + Aggregation
* JOIN implementation
* B+ Tree indexing
* Query optimization layer
* Transaction support

---

## 🎯 Learning Outcomes

This project helped in understanding:

* DBMS internal working
* Query parsing & execution
* Indexing and optimization
* File-based storage systems
* Clean OOP design
* System design thinking

---

## 👨‍💻 Author

**Kartikeya Gupta**
Computer Science (AI & DS)
GL Bajaj Group of Institution, Mathura