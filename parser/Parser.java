package parser;

import java.util.ArrayList;
import java.util.List;

import query.Condition;
import query.CreateTableQuery;
import query.InsertQuery;
import query.Query;
import query.SelectQuery;
import query.UpdateQuery;
import query.DeleteQuery;
import storage.Column;
import storage.DataType;

public class Parser {
    public Query parse(String input) {
        input = preprocess(input);
        String[] tokens = tokenize(input);
        if (tokens.length == 0 || tokens[0].isEmpty()) {
            throw new RuntimeException("Empty query");
        }
        for (int i = 0; i < tokens.length; i++) {

            if (!tokens[i].startsWith("\"")) {
                tokens[i] = tokens[i].toUpperCase();
            }
        }
        
        String firstToken = tokens[0].toUpperCase();

        switch (firstToken) {
            case "CREATE":
                return parseCreate(tokens);
            case "INSERT":
                return parseInsert(tokens);
            case "SELECT":
                return parseSelect(tokens);
            case "UPDATE":
                return parseUpdate(tokens);
            case "DELETE":
                return parseDelete(tokens);
            default:
                throw new RuntimeException("Unsupported query");
        }
    }

    private String preprocess(String input) {
        input = input.trim();
        if (input.endsWith(";")) {
            input = input.substring(0, input.length() - 1);
        }

        input = input.replace("(", " ( ");
        input = input.replace(")", " ) ");
        input = input.replace(",", " , ");
        input = input.replace("=", " = ");

        input = input.replaceAll("\\s+", " ");
        return input;
    }

    private String[] tokenize(String input) {
        return input.split("\\s+");
    }

    private Query parseCreate(String[] tokens) {
        if (tokens.length < 4 || !tokens[1].equalsIgnoreCase("TABLE")) {
            throw new RuntimeException("Invalid CREATE TABLE syntax");
        }

        String tableName = tokens[2];
        int openParenIndex = -1;
        int closeParenIndex = -1;

        int startIndex = 3;

        int arr[] = findParentheses(tokens, startIndex);
        openParenIndex = arr[0];
        closeParenIndex = arr[1];

        if (openParenIndex == -1 || closeParenIndex == -1 || closeParenIndex <= openParenIndex) {
            throw new RuntimeException("Invalid CREATE query: missing parentheses");
        }

        List<Column> columns = new ArrayList<>();

        int i = openParenIndex + 1;

        while (i < closeParenIndex) {
            String columnName = tokens[i];

            if (i + 1 >= closeParenIndex) {
                throw new RuntimeException("Missing datatype for column: " + columnName);
            }

            String typeToken = tokens[i + 1];
            DataType type;

            try {
                type = DataType.valueOf(typeToken.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid datatype: " + typeToken);
            }
            columns.add(new Column(columnName, type));
            i += 2;

            if (i < closeParenIndex && tokens[i].equals(",")) {
                i++;
            }
        }

        if (columns.isEmpty()) {
            throw new RuntimeException("CREATE TABLE must define at least one column");
        }

        return new CreateTableQuery(tableName, columns);
    }

    private Query parseInsert(String[] tokens) {
        if (tokens.length < 4 || !tokens[1].equalsIgnoreCase("INTO")) {
            throw new RuntimeException("Invalid INSERT INTO syntax");
        }
        String tableName = tokens[2];

        if (!tokens[3].equalsIgnoreCase("VALUES")) {
            throw new RuntimeException("Invalid INSERT INTO syntax: missing VALUES");
        }

        int valuesIndex = 3;

        int openParenIndex = -1;
        int closeParenIndex = -1;
        int arr[] = findParentheses(tokens, valuesIndex + 1);
        openParenIndex = arr[0];
        closeParenIndex = arr[1];

        if (openParenIndex == -1 || closeParenIndex == -1 || closeParenIndex <= openParenIndex) {
            throw new RuntimeException("Invalid INSERT query: missing parentheses");
        }

        if (openParenIndex + 1 == closeParenIndex) {
            throw new RuntimeException("VALUES cannot be empty");
        }

        List<Object> values = new ArrayList<>();
        boolean expectingValue = true;

        for (int i = openParenIndex + 1; i < closeParenIndex; i++) {
            String token = tokens[i];
            if (expectingValue) {
                if (token.equals(",")) {
                    throw new RuntimeException("Expected value but found comma");
                }
                values.add(parseLiteral(token));
                expectingValue = false;
            } else {
                if (!token.equals(",")) {
                    throw new RuntimeException("Expected comma but found: " + token);
                }
                expectingValue = true;
            }
        }

        if (expectingValue) {
            throw new RuntimeException("Trailing comma in VALUES clause");
        }
        return new InsertQuery(tableName, values);
    }

    private Query parseSelect(String[] tokens) {
        if (tokens.length < 4) {
            throw new RuntimeException("Invalid SELECT syntax");
        }

        int fromIndex = findKeywordIndex(tokens, "FROM");
        if (fromIndex == -1) {
            throw new RuntimeException("Invalid SELECT syntax: missing FROM");
        }

        List<String> selectedColumns = new ArrayList<>();
        boolean selectAll = false;
        for (int i = 1; i < fromIndex; i++) {
            String token = tokens[i];
            if (token.equals("*")) {
                if (selectedColumns.size() > 0) {
                    throw new RuntimeException("Cannot select specific columns when using *");
                }
                selectAll = true;
            } else {
                if (!token.equals(",")) {
                    selectedColumns.add(token);
                }
            }
        }

        if (selectAll && selectedColumns.size() > 0) {
            throw new RuntimeException("Cannot select specific columns when using *");
        }

        String tableName = tokens[fromIndex + 1];
        if (tableName.equalsIgnoreCase("WHERE")) {
            throw new RuntimeException("Missing table name in SELECT query");
        }

        Condition whereCondition = null;
        int whereIndex = findKeywordIndex(tokens, "WHERE");

        if (whereIndex != -1) {
            if (whereIndex < fromIndex) {
                throw new RuntimeException("WHERE clause appears before FROM");
            }
            whereCondition = parseCondition(tokens, whereIndex + 1);
        }
        return new SelectQuery(tableName, selectedColumns, selectAll, whereCondition);
    }

    private Query parseUpdate(String[] tokens) {
        if (tokens.length < 6) {
            throw new RuntimeException("Invalid UPDATE syntax");
        }

        String tableName = tokens[1];
        int setIndex = findKeywordIndex(tokens, "SET");
        if (setIndex == -1) {
            throw new RuntimeException("Invalid UPDATE syntax: missing SET");
        }

        String columnName = tokens[setIndex + 1];

        if (setIndex + 2 >= tokens.length || !tokens[setIndex + 2].equals("=")) {
            throw new RuntimeException("Invalid UPDATE syntax: missing '='");
        }

        if (setIndex + 3 >= tokens.length) {
            throw new RuntimeException("Invalid UPDATE syntax: missing value");
        }

        Object newValue = parseLiteral(tokens[setIndex + 3]);

        Condition whereCondition = null;
        int whereIndex = findKeywordIndex(tokens, "WHERE");
        if (whereIndex != -1) {
            if (whereIndex < setIndex) {
                throw new RuntimeException("WHERE clause appears before SET");
            }
            whereCondition = parseCondition(tokens, whereIndex + 1);
        }

        return new UpdateQuery(tableName, columnName, newValue, whereCondition);
    }

    private Query parseDelete(String[] tokens) {
        if (tokens.length < 3) {
            throw new RuntimeException("Invalid DELETE syntax");
        }

        if (!tokens[1].equalsIgnoreCase("FROM")) {
            throw new RuntimeException("Invalid DELETE syntax: missing FROM");
        }

        String tableName = tokens[2];

        Condition whereCondition = null;
        int whereIndex = findKeywordIndex(tokens, "WHERE");
        if (whereIndex != -1) {
            if (whereIndex < 3) {
                throw new RuntimeException("WHERE clause appears before table name");
            }
            whereCondition = parseCondition(tokens, whereIndex + 1);
        }

        return new DeleteQuery(tableName, whereCondition);
    }

    private Object parseLiteral(String token) {
        if (token.startsWith("\"")) {
            if (!token.endsWith("\"")) {
                throw new RuntimeException("Strings with spaces are not supported yet");
            }
            return token.substring(1, token.length() - 1);
        } else if (token.matches("-?\\d+")) {
            return Integer.parseInt(token);
        } else {
            throw new RuntimeException("Invalid literal: " + token);
        }
    }

    private int[] findParentheses(String[] tokens, int startIndex) {
        int openParenIndex = -1;
        int closeParenIndex = -1;
        for (int i = startIndex; i < tokens.length; i++) {
            if (tokens[i].equals("(")) {
                openParenIndex = i;
            } else if (tokens[i].equals(")")) {
                closeParenIndex = i;
                break;
            }
        }
        return new int[] { openParenIndex, closeParenIndex };
    }

    private int findKeywordIndex(String[] tokens, String keyword) {
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equalsIgnoreCase(keyword)) {
                return i;
            }
        }
        return -1;
    }

    private Condition parseCondition(String tokens[], int startIndex) {
        if (tokens.length - startIndex < 3) {
            throw new RuntimeException("Invalid WHERE clause");
        }
        if (tokens.length > startIndex + 3) {
            throw new RuntimeException("Invalid WHERE clause");
        }
        String columnName = tokens[startIndex];
        String operator = tokens[startIndex + 1];
        if (!operator.equals("=")) {
            throw new RuntimeException("Only '=' operator supported in WHERE");
        }
        String valueToken = tokens[startIndex + 2];
        Object value = parseLiteral(valueToken);
        return new Condition(columnName, operator, value);
    }
}
