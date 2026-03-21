package query;

import java.util.List;
import executor.QueryExecutor;

public class InsertQuery implements Query {
    private String tableName;
    private List<Object> values;

    public InsertQuery(String tableName, List<Object> values) {
        this.tableName = tableName;
        this.values = values;
    }

    public String getTableName() {
        return tableName;
    }

    public List<Object> getValues() {
        return values;
    }

    @Override
    public void accept(QueryExecutor executor) {
        executor.executeInsert(this);
    }
}
