package storage;

import java.util.List;

public class Row {
    private final List<Object> values;

    public Row(List<Object> values) {
        this.values = values;
    }

    public Object getValue(int index) {
        return values.get(index);
    }

    public List<Object> getAllValues() {
        return values;
    }
}