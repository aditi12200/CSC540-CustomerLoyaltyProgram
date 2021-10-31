import java.sql.*;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Scanner;


public class Login {
    public static String userType = "";

    public static void loginPage() {
        Scanner sc = new Scanner(System.in);
        boolean success = false;

        do {
            System.out.print("Enter your userid:");
            String userid = sc.nextLine();
            System.out.print("Enter your password:");
            String password = sc.nextLine();

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
            //TODO: go to Brand dashboard
        } else
        {
            //TODO: go to Customer dashboard
        }

    }

    private static boolean checkIdAndPass(String userid, String password) {
        boolean success = false;
        //todo: update query
        String sqlquery = "select usertype from users where userid =  '" + userid
                + "' and password='" + password + "'";

        ResultSet rs = null;
        try {
            rs = Mainmenu.statement.executeQuery(sqlquery);
            if (rs.next()) {
                userType = rs.getString("USERTYPE"); //update usertype
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