package executor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import query.Condition;
import query.CreateTableQuery;
import query.DeleteQuery;
import query.InsertQuery;
import query.SelectQuery;
import query.UpdateQuery;
import storage.Database;
import storage.Table;
import storage.Row;

public class QueryExecutor {
    private Database database;

    public QueryExecutor(Database database) {
        this.database = database;
    }

    public void executeCreate(CreateTableQuery query) {
        database.createTable(query.getTableName(), query.getColumns());
    }

    public void executeInsert(InsertQuery query) {
        Table table = database.getTable(query.getTableName());
        table.insertRow(query.getValues());
    }

    public void executeSelect(SelectQuery query) {
        Table table = database.getTable(query.getTableName());
        List<String> columns;

        if (query.isSelectAll()) {
            columns = table.getColumnNames();
        } else {
            columns = query.getSelectedColumns();
        }

        List<Integer> columnIndexes = new ArrayList<>();
        for (String col : columns) {
            columnIndexes.add(table.getColumnIndex(col));
        }

        Condition condition = query.getWhereCondition();
        boolean hasCondition = condition != null;
        int conditionIndex = -1;
        Object conditionValue = null;

        if (hasCondition) {
            conditionIndex = table.getColumnIndex(condition.getColumnName());
            conditionValue = condition.getValue();
        }

        for (String col : columns) {
            System.out.print(col + " ");
        }
        System.out.println();

        List<Row> rows = new ArrayList<>();

        for (Row row : table.getRows()) {
            if (hasCondition) {
                Object rowValue = row.getValue(conditionIndex);
                if (!conditionValue.equals(rowValue)) {
                    continue;
                }
            }
            rows.add(row);
        }

        if (query.getOrderByColumn() != null) {
            int orderByIndex = table.getColumnIndex(query.getOrderByColumn());
            rows.sort((r1, r2) -> {
                Comparable v1 = (Comparable) r1.getValue(orderByIndex);
                Comparable v2 = (Comparable) r2.getValue(orderByIndex);
                int cmp = v1.compareTo(v2);
                return query.isOrderByAsc() ? cmp : -cmp;
            });
        }

        for (Row row : rows) {
            for (int index : columnIndexes) {
                System.out.print(row.getValue(index) + " ");
            }
            System.out.println();
        }
    }

    public void executeUpdate(UpdateQuery query) {
        String tableName = query.getTableName();
        Table table = database.getTable(tableName);

        String columnToUpdate = query.getColumnName();
        int columnIndex = table.getColumnIndex(columnToUpdate);

        Object newValue = query.getNewValue();

        Condition condition = query.getWhereCondition();
        boolean hasCondition = condition != null;
        int conditionIndex = -1;
        Object conditionValue = null;

        int updated = 0;

        if (hasCondition) {
            conditionIndex = table.getColumnIndex(condition.getColumnName());
            conditionValue = condition.getValue();
        }

        for (Row row : table.getRows()) {
            if (hasCondition) {
                Object rowValue = row.getValue(conditionIndex);
                if (!conditionValue.equals(rowValue)) {
                    continue;
                }
            }
            row.setValue(columnIndex, newValue);
            updated++;
        }
        if (updated > 0) {
            table.rewriteFile();
        }
        System.out.println("Updated " + updated + " row(s).");
    }

    public void executeDelete(DeleteQuery query) {
        String tableName = query.getTableName();
        Table table = database.getTable(tableName);

        Condition condition = query.getWhereCondition();
        boolean hasCondition = condition != null;
        int conditionIndex = -1;
        Object conditionValue = null;

        int deleted = 0;

        if (hasCondition) {
            conditionIndex = table.getColumnIndex(condition.getColumnName());
            conditionValue = condition.getValue();
        }

        Iterator<Row> iterator = table.getRows().iterator();

        while (iterator.hasNext()) {
            Row row = iterator.next();

            if (hasCondition) {
                Object rowValue = row.getValue(conditionIndex);
                if (!conditionValue.equals(rowValue)) {
                    continue;
                }
            }
            iterator.remove();
            deleted++;
        }
        if (deleted > 0) {
            table.rewriteFile();
        }
        System.out.println("Deleted " + deleted + " row(s).");
    }
}
