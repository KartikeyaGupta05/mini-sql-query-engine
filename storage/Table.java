package storage;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Table {
    private String tableName;
    private List<Column> columns;
    private Map<String, Integer> columnIndexMap;
    private List<Row> rows;

    public Table(String tableName, List<Column> columns) {
        this.tableName = tableName;
        this.columns = columns;
        this.columnIndexMap = new HashMap<>();
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            String columnName = column.getName();
            if (columnIndexMap.containsKey(columnName)) {
                throw new IllegalArgumentException("Duplicate column name: " + columnName);
            }
            columnIndexMap.put(columnName, i);
        }
        this.rows = new ArrayList<>();
    }

    public void insertRow(List<Object> values) {
        if (values.size() != columns.size()) {
            throw new IllegalArgumentException("Number of values must match number of columns");
        }
        rows.add(new Row(new ArrayList<>(values)));

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("data/" + tableName + ".table", true));

            for (int i = 0; i < values.size(); i++) {
                Object value = values.get(i);
                writer.write(value.toString());
                if (i < values.size() - 1) {
                    writer.write(",");
                }
            }
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Error inserting row into table: " + tableName, e);
        }
    }

    public void loadRow(List<Object> values) {
        rows.add(new Row(new ArrayList<>(values)));
    }

    public void rewriteFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("data/" + tableName + ".table"));

            for (int i = 0; i < columns.size(); i++) {
                Column col = columns.get(i);
                writer.write(col.getName() + ":" + col.getType());
                if (i < columns.size() - 1) {
                    writer.write(",");
                }
            }
            writer.newLine();

            for (Row row : rows) {
                List<Object> values = row.getAllValues();

                for (int i = 0; i < values.size(); i++) {
                    writer.write(values.get(i).toString());
                    if (i < values.size() - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }

            writer.close();
        } catch (Exception e) {
            throw new RuntimeException("Error rewriting table file: " + tableName, e);
        }
    }

    public String getTableName() {
        return tableName;
    }

    public List<Column> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public List<String> getColumnNames() {
        List<String> columnNames = new ArrayList<>();
        for (Column column : columns) {
            columnNames.add(column.getName());
        }
        return Collections.unmodifiableList(columnNames);
    }

    public int getColumnIndex(String columnName) {
        Integer index = columnIndexMap.get(columnName);
        if (index == null) {
            throw new IllegalArgumentException("Column not found: " + columnName);
        }
        return index;
    }

    public List<Row> getRows() {
        return rows;
    }
}