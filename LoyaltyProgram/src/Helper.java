import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Helper {
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (Throwable e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void close(Statement stmt) {
        if (st != null) {
            try {
                stmt.close();
            } catch (Throwable e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Throwable e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static int selectNextOption(Scanner sc, String optionText) {
        boolean selected = false;
        int selection;

        do{
            System.out.println("Choose one of the following options");
            System.out.println("1. " + optionText);
            System.out.println("2. Go Back");

            try {
                selection = sc.nextInt();
                if (selection != 1 && selection != 2) {
                    System.out.println("You have made an invalid choice. Please choose again.");
                } else
                {
                    selected = true;
                }
            } catch (Exception e) {
                System.out.println("Please pick an option between 1 and 2.");
                sc.next();
            }
        } while(!selected);

        return selection;
    }
}