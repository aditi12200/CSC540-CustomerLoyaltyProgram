import java.util.Scanner;
import java.util.*;
import java.sql.*;


public class RewardType {
    public static Map<Integer, String> rewardCategories=new HashMap<Integer, String>();
    public static Map<String, String> rewardIdCatMap=new HashMap<String, String>();

    public static void rewardTypePage() {
        Scanner sc = new Scanner(System.in);

        int enteredValue, quantity;
        boolean goBack = false;
        initialize();

        do {
            System.out.println("Enter quantity for chosen reward category");
            quantity=sc.nextInt();
            System.out.println("Choose one of the following options");
            for (Map.Entry<Integer,String> entry : rewardCategories.entrySet()) {
                System.out.println(entry.getKey() + ". " + entry.getValue());
            }
            int nextOptNum=rewardCategories.size()+1;
            System.out.println(nextOptNum+". Go Back");

            enteredValue=sc.nextInt();

            if(enteredValue==nextOptNum) {
                goBack=true;
                goBack();
            } else if(rewardCategories.containsKey(enteredValue)) {
                checkIfRuleAlreadyExistsFOrThisCategory(rewardCategories.get(enteredValue));
                addRewardType(rewardCategories.get(enteredValue), quantity);
            } else {
                System.out.println("You have made an invalid choice. Please pick again.");
            }
        } while (!goBack);
    }

    public static void initialize() {
        try {
            String rewCategorySelectSql = "select RT_ID, REWARD_NAME from REWARD_TYPE";

            ResultSet rs = MainMenu.statement.executeQuery(rewCategorySelectSql);

            int i=1;
            while (rs.next()) {
                rewardCategories.put(i++, rs.getString("REWARD_NAME"));
                rewardIdCatMap.put(rs.getString("REWARD_NAME"), rs.getString("RT_ID"));
            }
        } catch (SQLException e) {
            System.out.println("Reward categories could not be fetched. Please try again.");
        }
    }

    public static void addRewardType(String rewardName, int quantity) {
        Scanner sc=new Scanner(System.in);
        String sql;
        String value;
        String rcc;
        rcc=rewardIdCatMap.get(rewardName);
        PreparedStatement ps=null;

        if (rewardName.equalsIgnoreCase("gift card")){
            System.out.println("Enter the value for the gift card:");
            value=sc.nextLine();

            try{
                sql="Insert into REWARD(REWARD_CATEGORY_CODE, VALUE, QUANTITY, BRAND_ID) values (?,?,?,?)";
                ps=MainMenu.connection.prepareStatement(sql);
                ps.setString(1,rcc);
                ps.setString(2,value);
                ps.setInt(3,quantity);
                ps.setString(4,Login.userId);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("Reward Type has been added successfully.");
                } else {
                    System.out.println("Reward Type could not be added. Please try again.");
                }

            }catch(SQLIntegrityConstraintViolationException e){
                System.out.println("Reward Type already present.");
            }catch (SQLException e) {
                System.out.println("Reward Type could not be added. Please try again.");
            }
        } else{
            try{
                sql="Insert into REWARD(REWARD_CATEGORY_CODE, QUANTITY, BRAND_ID) values (?,?,?)";
                ps=MainMenu.connection.prepareStatement(sql);
                ps.setString(1,rcc);
                ps.setInt(2,quantity);
                ps.setString(3,Login.userId);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("Reward Type has been added successfully.");
                } else {
                    System.out.println("Reward Type could not be added. Please try again.");
                }

            } catch (SQLIntegrityConstraintViolationException e) {
                System.out.println("Reward Type already present.");
            } catch (SQLException e) {
                System.out.println("Reward Type can not be added. Please try again.");
            }
        }
    }

    public static void checkIfRuleAlreadyExistsFOrThisCategory(String rewardName) {
        String rcc=rewardIdCatMap.get(rewardName);
        try {
            String sqlBrandRccSelect = "select * from REWARD where BRAND_ID  = '" + Login.userId + "' AND REWARD_CATEGORY_CODE = '" + rcc + "'";

            ResultSet rs = MainMenu.statement.executeQuery(sqlBrandRccSelect);

            if (rs.next()) {
                System.out.println("Reward Type already present.");
                rewardTypePage();
            }
        } catch (SQLException e) {
            System.out.println("Reward category data for this brand could not be fetched. Please try again.");
        }
    }

    public static void goBack() {
        if (LoyaltyProgram.lpType.equalsIgnoreCase("R")) {
            Regular.regularPage();
        } else {
            Tier.tierPage();
        }
    }
}
