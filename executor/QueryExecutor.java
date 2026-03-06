package executor;

import java.util.ArrayList;
import java.util.List;

import query.Condition;
import query.CreateTableQuery;
import query.InsertQuery;
import query.SelectQuery;
import storage.Database;
import storage.Table;
import storage.Row;

public class QueryExecutor {
    private Database database;

    public QueryExecutor(Database database) {
        this.database = database;
    }

    public void executeCreate(CreateTableQuery query) {
        database.createTable(query.getTableName(), query.getColumnNames());
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

        for (Row row : table.getRows()) {
            if (hasCondition) {
                Object rowValue = row.getValue(conditionIndex);
                if (!rowValue.equals(conditionValue)) {
                    continue;
                }
            }
            for (int index : columnIndexes) {
                System.out.print(row.getValue(index) + " ");
            }
            System.out.println();
        }
    }
}
