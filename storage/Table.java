package storage;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


public class Table {
    private String tableName;
    private List<String> columnNames;
    private Map<String, Integer> columnIndexMap; // Column name to index mapping
    private List<Row> rows;

    public Table(String tableName, List<String> columnNames) {
        this.tableName = tableName;
        this.columnNames = columnNames;
        this.columnIndexMap = new HashMap<>();
        for (int i = 0; i < columnNames.size(); i++) {
            String columnName = columnNames.get(i);
            if (columnIndexMap.containsKey(columnName)) {
                throw new IllegalArgumentException("Duplicate column name: " + columnName);
            }
            columnIndexMap.put(columnName, i);
        }
        this.rows = new ArrayList<>();
    }


    public void insertRow(List<Object> values) {
        if(values.size() != columnNames.size()) {
            throw new IllegalArgumentException("Number of values must match number of columns");
        }
        rows.add(new Row(values));
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumnNames() {
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
        return Collections.unmodifiableList(rows);
    }
}