package query;

import executor.QueryExecutor;

public class UpdateQuery implements Query {
    private String tableName;
    private String columnName;
    private Object newValue;
    private Condition whereCondition;

    public UpdateQuery(String tableName, String columnName, Object newValue, Condition whereCondition) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.newValue = newValue;
        this.whereCondition = whereCondition;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public Object getNewValue() {
        return newValue;
    }

    public Condition getWhereCondition() {
        return whereCondition;
    }


    @Override
    public void accept(QueryExecutor executor) {
        executor.executeUpdate(this);
    }
}