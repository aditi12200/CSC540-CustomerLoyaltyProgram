
import java.sql.*;
import java.util.*;

public class MainMenu {

    public static final String jdbcURL = "jdbc:oracle:thin:@ora.csc.ncsu.edu:1521:orcl01";
    public static final String user = "araghut";
    public static final String passwd = "200421320";

    public static Connection connection;
    public static Statement statement;

    public static void main(String[] args) {
        try {

            Class.forName("oracle.jdbc.OracleDriver");

            try {

                connection = DriverManager.getConnection(jdbcURL, user, passwd);
                statement = connection.createStatement();

                System.out.println("\t\tWelcome to Customer Loyalty Program.\n\n");
                displayMenu();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                exitProgram();
            }
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
    }

    public static void displayMenu() {
        Scanner sc = new Scanner(System.in);

        int enteredValue=0;

        do {
            System.out.println("Select from the options below");
            System.out.println("1. Login");
            System.out.println("2. Sign Up");
            System.out.println("3. Show Queries");
            System.out.println("4. Exit");
            System.out.print("Enter your option:");

            try {
                enteredValue = sc.nextInt();

                switch (enteredValue) {
                    case 1:
                        Login.loginPage();
                        break;
                    case 2:
                        Signup.signUpPage();
                        break;
                    case 3:
                        ShowQuery.showQueryPage();
                        break;
                    case 4:
                        exitProgram();
                        break;
                    default:
                        System.out.println("You have made an invalid choice. Please pick again.");
                }
            } catch (Exception e) {
                System.out.println("You have made an invalid choice. Please pick again.");
                displayMenu();
            }
        } while (enteredValue != 4);
    }

    public static void exitProgram() {
        Helper.close(connection);
        Helper.close(statement);
        System.exit((0));
    }
}
