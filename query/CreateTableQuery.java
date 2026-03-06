package query;

import executor.QueryExecutor;
import java.util.List;

public class CreateTableQuery implements Query {

    private String tableName;
    private List<String> columnNames;

    public CreateTableQuery(String tableName, List<String> columnNames) {
        this.tableName = tableName;
        this.columnNames = columnNames;
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    @Override
    public void accept(QueryExecutor executor) {
        executor.executeCreate(this);
    }
}
