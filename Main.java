import parser.Parser;
import validator.QueryValidator;
import validator.ValidationException;
import executor.QueryExecutor;
import storage.Database;
import query.Query;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Database database = new Database();
        Parser parser = new Parser();
        QueryValidator validator = new QueryValidator(database);
        QueryExecutor executor = new QueryExecutor(database);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Mini SQL Engine Started. Type 'exit' to quit.");

        while (true) {
            System.out.print("SQL> ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }
            try {
                Query query = parser.parse(input);
                validator.validate(query);
                query.accept(executor);

            } catch (ValidationException e) {
                System.out.println("Validation Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error: " + e.getClass().getSimpleName());
                e.printStackTrace();
            }
        }
        scanner.close();
        System.out.println("SQL Engine stopped.");
    }
}