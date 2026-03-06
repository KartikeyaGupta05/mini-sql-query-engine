package query;

import executor.QueryExecutor;

public interface Query {
    void accept(QueryExecutor executor);
}
