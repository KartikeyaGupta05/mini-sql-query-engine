package query;

import executor.QueryExecutor;

public class DeleteQuery implements Query {
    private String tableName;
    private Condition whereCondition;

    public DeleteQuery(String tableName, Condition whereCondition) {
        this.tableName = tableName;
        this.whereCondition = whereCondition;
    }

    public String getTableName() {
        return tableName;
    }

    public Condition getWhereCondition() {
        return whereCondition;
    }
    
    @Override
    public void accept(QueryExecutor executor) {
        executor.executeDelete(this);
    }
}
