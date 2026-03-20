package storage;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Database {
    private Map<String, Table> tables;

    public Database() {
        this.tables = new HashMap<>();
    }

    public void createTable(String tableName, List<Column> columns) {
        if (tables.containsKey(tableName)) {
            throw new IllegalArgumentException("Table already exists: " + tableName);
        }

        Table newTable = new Table(tableName, columns);
        tables.put(tableName, newTable);

        try {
            File file = new File("data/" + tableName + ".table");

            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < columns.size(); i++) {
                Column column = columns.get(i);
                writer.write(column.getName() + ":" + column.getType());
                if (i < columns.size() - 1) {
                    writer.write(",");
                }
            }
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Error to make table schema: " + tableName, e);
        }
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

    public void loadData() {
        File folder = new File("data");
        if (!folder.exists() || !folder.isDirectory()) {
            return;
        }

        for (File file : folder.listFiles()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String tableName = file.getName().replace(".table", "");
                String schemaLine = reader.readLine();
                String[] columnParts = schemaLine.split(",");

                List<Column> columns = new ArrayList<>();
                for (String columnPart : columnParts) {
                    String[] parts = columnPart.split(":");
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Invalid column definition: " + columnPart);
                    }
                    String columnName = parts[0];
                    DataType columnType = DataType.valueOf(parts[1].toUpperCase());
                    columns.add(new Column(columnName, columnType));
                }

                Table table = new Table(tableName, columns);

                String rowLine;
                while ((rowLine = reader.readLine()) != null) {
                    String[] valueParts = rowLine.split(",");
                    List<Object> values = new ArrayList<>();
                    for (int i = 0; i < valueParts.length; i++) {
                        Column column = columns.get(i);
                        String valuePart = valueParts[i];
                        Object value;
                        switch (column.getType()) {
                            case INT:
                                value = Integer.parseInt(valuePart);
                                break;
                            case STRING:
                                value = valuePart;
                                break;
                            default:
                                throw new IllegalArgumentException("Unsupported data type: " + column.getType());
                        }
                        values.add(value);
                    }
                    table.loadRow(values);
                }

                tables.put(tableName, table);
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException("Error reading table data: " + file.getName(), e);
            }
        }
    }
}