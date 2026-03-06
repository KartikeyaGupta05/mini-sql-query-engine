package query;

public class Condition {
    private String columnName;
    private String operator;
    private Object value;

    public Condition(String columnName, String operator, Object value) {
        this.columnName = columnName;
        this.operator = operator;
        this.value = value;
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
}
