import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Scanner;
import java.util.*;
import java.sql.*;

public class Tier {
    public static void tierPage() {
        Scanner sc = new Scanner(System.in);
        int enteredValue;
        boolean selected = false;

        do {
            System.out.println("Choose one of the following options");
            System.out.println("1. Tiers Set up");
            System.out.println("2. Activity Types");
            System.out.println("3. Reward Types");
            System.out.println("4. Go Back");

            try {
                enteredValue = sc.nextInt();
                selected = true;

                switch (enteredValue) {
                    case 1:
                        tierSetupPage();
                        break;
                    case 2:
                        ActivityType.activityTypePage();
                        break;
                    case 3:
                        RewardType.rewardTypePage();
                        break;
                    case 4:
                        Brand.brandPage();
                        break;
                    default:
                        System.out.println("You have made an invalid choice. Please pick again.");
                        selected = false;
                }

            } catch (Exception e) {
                System.out.println("You have made an invalid choice. Please pick again.");
                sc.next();
            }
        } while (!selected);
    }

    private static void tierSetupPage() {
        boolean correctTierCount = false;
        Scanner sc = new Scanner(System.in);
        int tiers = 0;

        do {
            System.out.println("Enter no. of tiers(minimum 1 and maximum 3): ");
            tiers = sc.nextInt();

            if (tiers > 0 && tiers < 4) {
                correctTierCount = true;
            } else {
                System.out.println("Please enter number of tiers between 1 and 3");
            }
        } while (!correctTierCount);

        boolean tierFlag = false;

        String[] tierNames = new String[3];
        int[] tierPoints = new int[3], tierMultipliers = new int[3];


        do {
            String tierName = "base";
            int tierPoint = 0, tierMultiplier = 1;

            for (int i = 0; i < tiers; i++) {
                sc.nextLine();
                System.out.println("Enter Tiers in increasing order of precedence");
                System.out.println("Enter Tier " + (i + 1) + " Name:");
                tierName = sc.nextLine();
                if (i != 0) {
                    System.out.println("Enter Tier" + (i + 1) + " Points Required:");
                    tierPoint = sc.nextInt();
                    System.out.println("Enter Tier" + (i + 1) + " Points Multiplier:");
                    tierMultiplier = sc.nextInt();
                } else {
                    System.out.println("Points required will be 0 for base tier.");
                    System.out.println("Points multiplier will be 1 for base tier.");
                }

                tierNames[i] = tierName;
                tierPoints[i] = tierPoint;
                tierMultipliers[i] = tierMultiplier;
            }

            if (validateTier(tierNames, tierPoints, tierMultipliers, tiers)) {
                tierFlag = true;
            }
        } while (!tierFlag);

        int enteredValue = Helper.selectNextOption(sc, "Set up");

        if (enteredValue == 2) {
            tierPage();
        } else {
            addTiers(tierNames, tierPoints, tierMultipliers, tiers);
            tierPage();
        }
    }

    private static void addTiers(String[] tierNames, int[] tierPoints, int[] tierMultipliers, int tiers) {
        try {
            for (int i = 0; i < tiers; i++) {

                String sql = "Insert into TIER (TIER_NAME,BRAND_ID,PRECEDENCE,POINTS, MULTIPLIER) values (?,?,?,?,?)";
                PreparedStatement ps = MainMenu.connection.prepareStatement(sql);
                ps.setString(1, tierNames[i]);
                ps.setString(2,Login.userId);
                ps.setInt(3,i+1);
                ps.setInt(4, tierPoints[i]);
                ps.setInt(5, tierMultipliers[i]);


                ps.executeUpdate();
            }

            System.out.println("Tiers have been added successfully.");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Tiers name can not be same, Please try again.");
        } catch (SQLException e) {
            System.out.println("Tiers can not be added, Please try again.");
        }
    }

    private static boolean validateTier(String[] tierNames, int[] tierPoints, int[] tierMultipliers, int tiersCount) {
        boolean isValid = true;
        int[] points = tierPoints;
        int[] multiplier = tierMultipliers;
        Arrays.sort(points);
        Arrays.sort(multiplier);
        if(!Arrays.equals(points,tierPoints)) {
            isValid = false;
        }

        if(!Arrays.equals(multiplier,tierMultipliers)){
            isValid = false;
        }

        for (int i = 0; i < tiersCount; i++) {
            if (tierNames[i].equals("")) {
                System.out.println("Tier" + (i + 1) + " name can not be empty.");
                isValid = false;
            }
            if(tierPoints[0]!=0) {
                System.out.println("Tier 1 points should be zero" );
                isValid=false;
            }
            if(tierMultipliers[0]!=1) {
                System.out.println("Tier 1 multiplier should be one" );
                isValid=false;
            }
            if (i != 0 && tierPoints[i] == 0) {
                System.out.println("Tier" + (i + 1) + " points can not be 0 except base tier.");
                isValid = false;
            }

            if (i!=0 && tierMultipliers[i] < 2) {
                System.out.println("Tier" + (i + 1) + " multipliers can not be less than 2.");
                isValid = false;
            }
        }

        return isValid;
    }
}