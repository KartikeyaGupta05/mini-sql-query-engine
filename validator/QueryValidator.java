package validator;

import java.util.HashSet;
import java.util.Set;

import query.CreateTableQuery;
import query.InsertQuery;
import query.Query;
import query.SelectQuery;
import storage.Database;
import storage.Table;

public class QueryValidator {
    private Database database;

    public QueryValidator(Database database) {
        this.database = database;
    }

    public void validate(Query query) throws ValidationException {
        if (query instanceof CreateTableQuery) {
            validateCreateTableQuery((CreateTableQuery) query);
        } else if (query instanceof InsertQuery) {
            validateInsertQuery((InsertQuery) query);
        } else if (query instanceof SelectQuery) {
            validateSelectQuery((SelectQuery) query);
        } else {
            throw new ValidationException("Unsupported query type");
        }
    }

    private void validateCreateTableQuery(CreateTableQuery query) throws ValidationException {
        String tableName = query.getTableName();
        if (database.checkTableExists(tableName)) {
            throw new ValidationException("Table '" + tableName + "' already exists");
        }

        Set<String> seen = new HashSet<>();
        for (String col : query.getColumnNames()) {
            if (!seen.add(col)) {
                throw new ValidationException("Duplicate column: " + col);
            }
        }
        if (query.getColumnNames().isEmpty()) {
            throw new ValidationException("Table must have at least one column");
        }
    }

    private void validateInsertQuery(InsertQuery query) throws ValidationException {
        String tableName = query.getTableName();
        if (!database.checkTableExists(tableName)) {
            throw new ValidationException("Table '" + tableName + "' does not exist");
        }

        Table table = database.getTable(tableName);

        if (query.getValues().size() != table.getColumnNames().size()) {
            throw new ValidationException("Value count does not match column count for table '" + tableName + "'");
        }
    }

    private void validateSelectQuery(SelectQuery query) throws ValidationException {
        String tableName = query.getTableName();
        if (!database.checkTableExists(tableName)) {
            throw new ValidationException("Table '" + tableName + "' does not exist");
        }
        
        Table table = database.getTable(tableName);

        if (!query.isSelectAll()) {
            for (String column : query.getSelectedColumns()) {
                try {
                    table.getColumnIndex(column);
                } catch (IllegalArgumentException e) {
                    throw new ValidationException(
                            "Column '" + column + "' does not exist in table '" + tableName + "'");
                }
            }
        }

        if (query.getWhereCondition() != null) {
            String column = query.getWhereCondition().getColumnName();
            try {
                table.getColumnIndex(column);
            } catch (IllegalArgumentException e) {
                throw new ValidationException(
                        "Column '" + column + "' in WHERE clause does not exist in table '" + tableName + "'");
            }

        }
    }
}
