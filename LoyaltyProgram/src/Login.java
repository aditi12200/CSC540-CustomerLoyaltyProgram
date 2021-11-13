import java.sql.*;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Scanner;
import java.util.*;

public class Login {
    public static String userId, userType;

    public static void loginPage() {
        Scanner sc = new Scanner(System.in);
        boolean success = false;

        do {
            System.out.print("Enter your userid:");
            String userid = sc.nextLine();
            char[] pwd = System.console().readPassword("Enter password:");
            String password = pwd.toString();

            int option = Helper.selectNextOption(sc, "Sign in");

            if (option == 1) {
                success = checkIdAndPass(userid, password);
            } else {
                MainMenu.displayMenu();
            }

            sc.nextLine();
        } while (!success);

        if (userType.equalsIgnoreCase("Admin")) {
            Admin.adminPage();
        } else if(userType.equalsIgnoreCase("Brand"))
        {
            Brand.brandPage();
        } else
        {
            Customer.customerPage();
        }

    }

    private static boolean checkIdAndPass(String userid, String password) {
        boolean success = false;
        //todo: update query
        String sqlquery = "select TYPE from USERS where USER_ID =  '" + userid
                + "' and PWD='" + password + "'";

        ResultSet rs = null;
        try {
            rs = MainMenu.statement.executeQuery(sqlquery);
            if (rs.next()) {
                userType = rs.getString("TYPE"); //update usertype
                userId = userid;
                success = true;
            } else {
                System.out.println("Username and/or password is incorrect. Please try again.");
            }
            rs.close();
        } catch (SQLException e) {
            Helper.close(rs);
            e.printStackTrace();
        }

        return success;
    }

}