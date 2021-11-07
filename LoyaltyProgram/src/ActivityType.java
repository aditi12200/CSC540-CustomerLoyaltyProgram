import java.util.Scanner;
import java.sql.*;
import java.util.*;

public class ActivityType {
    public static Map<Integer, String> actCategories=new HashMap();
    public static void activityTypePage() {

        initialize();

        displayOptions();

    }

    private static void displayOptions() {
        Scanner sc = new Scanner(System.in);
        int enteredValue;
        boolean goBack = false;
        do {
            System.out.println("Choose one of the following options");
            for (Map.Entry<Integer,String> entry : actCategories.entrySet()) {
                System.out.println(entry.getKey() + ". " + entry.getValue());
            }
            int nextOptNum=actCategories.size()+1;
            System.out.println(nextOptNum+". Go Back");

            enteredValue=sc.nextInt();

            if(enteredValue==nextOptNum) {
                goBack=true;
                goBack();
            } else if(actCategories.containsKey(enteredValue)) {
                addActivityType(actCategories.get(enteredValue));
            } else {
                System.out.println("You have made an invalid choice. Please pick again.");
            }
        } while (!goBack);
    }

    private static void initialize() {
        try {
            String actCategorySelectSql = "select ACTIVITY_NAME from ACTIVITY_TYPE where VISIBLE = 'Y'";

            ResultSet rs = MainMenu.statement.executeQuery(actCategorySelectSql);

            int i=1;
            while (rs.next()) {
                actCategories.put(i++, rs.getString("ACTIVITY_NAME"));
            }
        } catch (SQLException e) {
            System.out.println("Activity categories could not be fetched. Please try again.");
            initialize();
        }
    }

    private static void addActivityType(String activityCategory) {
        String sqlActCatSelect = "Select AT_ID from ACTIVITY_TYPE WHERE ACTIVITY_NAME='"+activityCategory+"'";
        String acc;
        try {
            ResultSet rs = MainMenu.statement.executeQuery(sqlActCatSelect);

            if (rs.next()) {
                acc=rs.getString("AT_ID");
                String sql = "Insert into LP_ACT_CATEGORY(BRAND_ID, ACT_CATEGORY_CODE) values (?,?)";
                try {
                    PreparedStatement ps = MainMenu.connection.prepareStatement(sql);
                    ps.setString(1, Login.userId);
                    ps.setString(2, acc);

                    int rows = ps.executeUpdate();
                    if (rows == 1) {
                        System.out.println("Activity Type has been added successfully.");
                    } else {
                        System.out.println("Activity Type could not be added. Please try again.");
                    }
                    displayOptions();
                } catch (SQLIntegrityConstraintViolationException e) {
                    System.out.println("Activity Type already present.");
                    displayOptions();
                } catch (SQLException e) {
                    System.out.println("Activity Type could not be added. Please try again.");
                    displayOptions();
                }
            } else {
                System.out.println("Activity Type could not be found. Please try again.");
            }
            displayOptions();
        } catch (SQLException e) {
            System.out.println("Activity Type could not be found. Please try again.");
            displayOptions();
        }


    }

    private static void goBack() {
        if (LoyaltyProgram.lpType.equalsIgnoreCase("R")) {
            Regular.regularPage();
        } else {
            Tier.tierPage();
        }
    }
}
