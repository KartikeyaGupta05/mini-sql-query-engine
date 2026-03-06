package query;

import executor.QueryExecutor;
import java.util.List;

public class SelectQuery implements Query {
    private String tableName;
    private List<String> selectedColumn;
    private boolean selectAll;
    private Condition whereCondition;

    public SelectQuery(String tableName, List<String> selectedColumn, boolean selectAll, Condition whereCondition) {
        this.tableName = tableName;
        this.selectedColumn = selectedColumn;
        this.selectAll = selectAll;
        this.whereCondition = whereCondition;
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getSelectedColumns() {
        return selectedColumn;
    }

    public boolean isSelectAll() {
        return selectAll;
    }

    public Condition getWhereCondition() {
        return whereCondition;
    }

    @Override
    public void accept(QueryExecutor executor) {
        executor.executeSelect(this);
    }
}
