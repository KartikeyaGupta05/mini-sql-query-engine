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

        for (String col : columns) {
            System.out.print(col + " ");
        }
        System.out.println();

        List<Row> rows = new ArrayList<>();

        for (Row row : table.getRows()) {
            if (matches(row, condition, table)) {
                rows.add(row);
            }
        }

        if (query.getOrderByColumn() != null) {
            int orderByIndex = table.getColumnIndex(query.getOrderByColumn());
            rows.sort((r1, r2) -> {
                Object v1 = r1.getValue(orderByIndex);
                Object v2 = r2.getValue(orderByIndex);

                if (!(v1 instanceof Comparable) || !(v2 instanceof Comparable)) {
                    throw new RuntimeException("ORDER BY column is not comparable");
                }

                int cmp = ((Comparable) v1).compareTo(v2);
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

        int updated = 0;

        for (Row row : table.getRows()) {
            if (matches(row, condition, table)) {
                row.setValue(columnIndex, newValue);
                updated++;
            }
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

        int deleted = 0;
        Iterator<Row> iterator = table.getRows().iterator();

        while (iterator.hasNext()) {
            Row row = iterator.next();

            if (matches(row, condition, table)) {
                iterator.remove();
                deleted++;
            }
        }
        if (deleted > 0) {
            table.rewriteFile();
        }
        System.out.println("Deleted " + deleted + " row(s).");
    }

    private boolean evaluateCondition(Condition condition, Row row, Table table) {
        if (condition.isCompound()) {
            boolean leftResult = evaluateCondition(condition.getLeft(), row, table);
            boolean rightResult = evaluateCondition(condition.getRight(), row, table);
            if (condition.getLogicalOperator().equalsIgnoreCase("AND")) {
                return leftResult && rightResult;
            } else if (condition.getLogicalOperator().equalsIgnoreCase("OR")) {
                return leftResult || rightResult;
            } else {
                throw new RuntimeException("Unsupported logical operator: " + condition.getLogicalOperator());
            }
        }

        int columnIndex = table.getColumnIndex(condition.getColumnName());
        Object rowValue = row.getValue(columnIndex);
        Object conditionValue = condition.getValue();
        String op = condition.getOperator();
        if (!(rowValue instanceof Comparable) || !(conditionValue instanceof Comparable)) {
            throw new RuntimeException("Values are not comparable");
        }
        switch (op) {
            case "=":
                return rowValue.equals(conditionValue);
            case "<":
                return ((Comparable) rowValue).compareTo(conditionValue) < 0;
            case ">":
                return ((Comparable) rowValue).compareTo(conditionValue) > 0;
            default:
                throw new RuntimeException("Unsupported operator: " + condition.getOperator());
        }
    }

    private boolean matches(Row row, Condition condition, Table table) {
        return condition == null || evaluateCondition(condition, row, table);
    }
}
