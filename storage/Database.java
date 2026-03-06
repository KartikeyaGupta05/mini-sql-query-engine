package storage;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class Database {
    private Map<String, Table> tables;

    public Database() {
        this.tables = new HashMap<>();
    }

    public void createTable(String tableName, List<String> columnNames) {
        if (tables.containsKey(tableName)) {
            throw new IllegalArgumentException("Table already exists: " + tableName);
        }

        Table newTable = new Table(tableName, columnNames);
        tables.put(tableName, newTable);
    }

    public Table getTable(String tableName) {
        Table table = tables.get(tableName);
        if (table == null) {
            throw new IllegalArgumentException("Table not found: " + tableName);
        }
        return table;
    }

    public boolean checkTableExists(String tableName) {
        return tables.containsKey(tableName);
    }
}