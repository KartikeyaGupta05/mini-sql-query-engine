package query;

import executor.QueryExecutor;
import storage.Column;

import java.util.List;

public class CreateTableQuery implements Query {

    private String tableName;
    private List<Column> columns;

    public CreateTableQuery(String tableName, List<Column> columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    public String getTableName() {
        return tableName;
    }

    public List<Column> getColumns() {
        return columns;
    }

    @Override
    public void accept(QueryExecutor executor) {
        executor.executeCreate(this);
    }
}
