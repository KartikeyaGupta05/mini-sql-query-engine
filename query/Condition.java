package query;

public class Condition {
    private String columnName;
    private String operator;
    private Object value;

    private Condition left;
    private Condition right;
    private String logicalOperator;

    // Constructor for simple condition
    public Condition(String columnName, String operator, Object value) {
        this.columnName = columnName;
        this.operator = operator;
        this.value = value;
    }

    // Constructor for compound condition or AND/OR condition
    public Condition(Condition left, String logicalOperator, Condition right) {
        this.left = left;
        this.logicalOperator = logicalOperator;
        this.right = right;
    }

    public boolean isCompound() {
        return logicalOperator != null;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getOperator() {
        return operator;
    }

    public Object getValue() {
        return value;
    }

    public Condition getLeft() {
        return left;
    }

    public Condition getRight() {
        return right;
    }

    public String getLogicalOperator() {
        return logicalOperator;
    }
}
