package validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import query.Condition;
import query.CreateTableQuery;
import query.InsertQuery;
import query.Query;
import query.SelectQuery;
import query.UpdateQuery;
import query.DeleteQuery;
import storage.Column;
import storage.DataType;
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
        } else if (query instanceof UpdateQuery) {
            validateUpdateQuery((UpdateQuery) query);
        } else if (query instanceof DeleteQuery) {
            validateDeleteQuery((DeleteQuery) query);
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
        for (Column col : query.getColumns()) {
            String colName = col.getName();
            if (!seen.add(colName)) {
                throw new ValidationException("Duplicate column: " + colName);
            }
        }
        if (query.getColumns().isEmpty()) {
            throw new ValidationException("Table must have at least one column");
        }
    }

    private void validateInsertQuery(InsertQuery query) throws ValidationException {
        String tableName = query.getTableName();
        if (!database.checkTableExists(tableName)) {
            throw new ValidationException("Table '" + tableName + "' does not exist");
        }

        Table table = database.getTable(tableName);

        List<Column> columns = table.getColumns();
        List<Object> values = query.getValues();

        if (values.size() != columns.size()) {
            throw new ValidationException("Value count does not match column count for table '" + tableName + "'");
        }

        for (int i = 0; i < columns.size(); i++) {
            DataType expectedType = columns.get(i).getType();
            Object value = values.get(i);
            if (expectedType == DataType.INT && !(value instanceof Integer)) {
                throw new ValidationException("Expected INT for column " + columns.get(i).getName());
            }
            if (expectedType == DataType.STRING && !(value instanceof String)) {
                throw new ValidationException("Expected STRING for column " + columns.get(i).getName());
            }
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
            validateCondition(query.getWhereCondition(), table, tableName);
        }

        if (query.getOrderByColumn() != null) {
            String column = query.getOrderByColumn();
            try {
                table.getColumnIndex(column);
            } catch (IllegalArgumentException e) {
                throw new ValidationException(
                        "Column '" + column + "' in ORDER BY clause does not exist in table '" + tableName + "'");
            }
        }
    }

    public void validateUpdateQuery(UpdateQuery query) throws ValidationException {
        String tableName = query.getTableName();
        if (!database.checkTableExists(tableName)) {
            throw new ValidationException("Table '" + tableName + "' does not exist");
        }

        Table table = database.getTable(tableName);
        String columnToUpdate = query.getColumnName();

        int columnIndex;
        try {
            columnIndex = table.getColumnIndex(columnToUpdate);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    "Column '" + columnToUpdate + "' to update does not exist in table '" + tableName + "'");
        }

        Object newValue = query.getNewValue();
        Column column = table.getColumns().get(columnIndex);
        DataType expectedType = column.getType();

        if (expectedType == DataType.INT && !(newValue instanceof Integer)) {
            throw new ValidationException("Expected INT for column " + column.getName());
        }
        if (expectedType == DataType.STRING && !(newValue instanceof String)) {
            throw new ValidationException("Expected STRING for column " + column.getName());
        }

        if (query.getWhereCondition() != null) {
            validateCondition(query.getWhereCondition(), table, tableName);
        }
    }

    public void validateDeleteQuery(DeleteQuery query) throws ValidationException {
        String tableName = query.getTableName();
        if (!database.checkTableExists(tableName)) {
            throw new ValidationException("Table '" + tableName + "' does not exist");
        }

        Table table = database.getTable(tableName);

        if (query.getWhereCondition() != null) {
            validateCondition(query.getWhereCondition(), table, tableName);
        }
    }

    private void validateCondition(Condition condition, Table table, String tableName)
            throws ValidationException {

        if (condition.isCompound()) {
            validateCondition(condition.getLeft(), table, tableName);
            validateCondition(condition.getRight(), table, tableName);
            return;
        }

        String column = condition.getColumnName();
        String op = condition.getOperator();

        if (!op.equals("=") && !op.equals("<") && !op.equals(">")) {
            throw new ValidationException("Unsupported operator: " + op);
        }

        try {
            table.getColumnIndex(column);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    "Column '" + column + "' in WHERE clause does not exist in table '" + tableName + "'");
        }
    }
}
