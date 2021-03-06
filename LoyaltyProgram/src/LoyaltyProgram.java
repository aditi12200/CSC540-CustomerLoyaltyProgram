import java.util.Scanner;
import java.sql.PreparedStatement;
import java.util.*;
import java.sql.*;

public class LoyaltyProgram {

    public static String lpType = "";

    public static void addLoyaltyProgram() {
        Scanner sc = new Scanner(System.in);
        int enteredValue;
        boolean selected = false;
        if(checkIfBrandIsEnrolled()){
            System.out.println("Brand is already enrolled in a Loyalty Program");
            enteredValue = Helper.selectNextOption(sc, "Add Tiers/Activity/Reward Types");
            if(enteredValue == 1){
                if(lpType.equalsIgnoreCase("R")){
                    Regular.regularPage();
                } else {
                    Tier.tierPage();
                }
            } else {
                Brand.brandPage();
            }
        }

        do {
            System.out.println("Choose one of the following options");
            System.out.println("1. Regular Program");
            System.out.println("2. Tiered Program");
            System.out.println("3. Go back");

            try {
                enteredValue = sc.nextInt();
                selected = true;

                switch (enteredValue) {
                    case 1:
                        insertLoyaltyProgram(Login.userId,"R");
                        Regular.regularPage();
                        break;
                    case 2:
                        insertLoyaltyProgram(Login.userId,"T");
                        Tier.tierPage();
                        break;
                    case 3:
                        Brand.brandPage();
                        break;
                    default:
                        System.out.println("You have made an invalid choice. Please pick again.");
                        selected = false;
                }

            } catch (Exception e) {
                System.out.println("Please pick an option between 1 and 3.");
                sc.next();
            }
        } while (!selected);
    }


    private static void insertLoyaltyProgram(String lpName, String lpType) {
        CallableStatement statement = null;
        PreparedStatement ps=null;
        try {

            ps = MainMenu.connection.prepareStatement("Insert into LOYALTY_PROGRAM (BRAND_LP_ID, TYPE, STATE) values (?,?,?)");
            ps.setString(1, Login.userId);
            ps.setString(2, lpType);
            ps.setString(3, "INACTIVE");
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Loyalty Program has been added successfully.");
            } else {
                System.out.println("Loyalty Program could not be added. Please try again.");
            }

            LoyaltyProgram.lpType = lpType;

            ps.close();

            //System.out.println("Loyalty Program has been successfully added, please proceed to add activity types, " +
            //        "reward types, and corresponding rules for activating the program.");

        } catch (SQLException e) {
            Helper.close(statement);
            System.out.println("Loyalty Program can not be added. Please try again.");
            addLoyaltyProgram();
        }
    }


    private static boolean checkIfBrandIsEnrolled() {
        boolean isEnrolled = false;

        String sql = "select type from LOYALTY_PROGRAM where BRAND_LP_ID =  '" + Login.userId + "'";

        ResultSet rs = null;
        try {
            rs = MainMenu.statement.executeQuery(sql);
            if (rs.next()) {
                lpType = rs.getString("TYPE");
                isEnrolled = true;
            }

            rs.close();
        } catch (SQLException e) {
            Helper.close(rs);
            e.printStackTrace();
        }

        return isEnrolled;
    }
}