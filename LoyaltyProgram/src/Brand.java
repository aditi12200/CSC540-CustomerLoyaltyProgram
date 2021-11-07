import java.util.Scanner;

public class Brand {
    //NOT SURE ABOUT THIS!
    public static boolean isEnrolled = false, isActive = false;
    public static String lpType = "R", lpName = "";

    public static void brandPage() {
        initialize();
        Scanner sc = new Scanner(System.in);
        int enteredValue;
        boolean selected = false;

        do {
            System.out.println("Choose one of the following options");
            System.out.println("1. Add Loyalty Program");
            System.out.println("2. Add reward earning rules(RER)");
            System.out.println("3. Update reward earning rules(RER)");
            System.out.println("4. Add reward redemption rules(RRR)");
            System.out.println("5. Update reward redemption rules(RRR)");
            System.out.println("6. Validate Loyalty Program");
            System.out.println("7. Log out");

            try {
                enteredValue = sc.nextInt();
                selected = true;

                if (!isEnrolled) {
                    if (selection >= 2 && selection <= 5) {
                        System.out.println("Brand is not enrolled in a program yet. Please enroll first.");
                        selected = false;
                        continue;
                    }
                } else {
                    if (selection == 6 && isActive) {
                        System.out.println("Loyalty program has been validated already and is in active state.");
                        selected = false;
                        continue;
                    }
                }

                switch (enteredValue) {
                    case 1:
                        LoyaltyProgram.addLoyaltyProgram();
                        break;
                    case 2:
                        addRERule();
                        break;
                    case 3:
                        updateRERule();
                        break;
                    case 4:
                        addRRRule();
                        break;
                    case 5:
                        updateRRRule();
                        break;
                    case 6:
                        validateLoyaltyProgram();
                        break;
                    case 7:
                        MainMenu.displayMenu();
                        break;
                    default:
                        System.out.println("You have made an invalid choice. Please pick again.");
                        selected = false;
                }

            } catch (Exception e) {
                System.out.println("Please pick an option between 1 and 7.");
                sc.next();
            }
        } while (!selected);
    }
    
    public static void addRERule() {
        String rerCode, activityCategoryId, numOfPoints;

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Reward Earning Rule code:");
        rerCode = sc.nextLine();
        System.out.print("Enter activity category id:");
        activityCategoryId = sc.nextLine();
        System.out.print("Enter number of points for this activity:");
        numOfPoints = sc.nextLine();

        int enteredValue = Helper.selectNextOption(sc, "Add RERule");
        CallableStatement statement = null;

        if (enteredValue == 2) {
            brandPage();
        } else {
            try {
                //TODO: Change query as per our tables
                statement = MainMenu.connection.prepareCall("{call add_rerule(?, ?, ?, ?, ?)}");
                statement.setString(1, Login.userId);
                statement.setString(2, rerCode);
                statement.setString(3, activityCategoryId);
                statement.setString(4, numOfPoints);
                statement.registerOutParameter(5, Types.INTEGER);

                statement.execute();

                int result = statement.getInt(5);

                statement.close();

                if (ret == 0) {
                    System.out.println("RERule with this code already present.");
                } else if(ret == 1) {
                    System.out.println("RERule added successfully.");
                } else if(ret == 2) {
                    System.out.println("This activty type is not part of your loyalty program!");
                } else {
                    System.out.println("RERule could not be added. Please try again.");
                }
                addRERule();
            } catch(SQLException e)
            {
                Helper.close(statement);
                System.out.println("RERule could not be added. Please try again.");
                addRERule();
            }
        }
    }

    public static void updateRERule() {
        String rerCode, activityCategoryId, numOfPoints;

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter code of Reward Earning Rule that you want to update:");
        rerCode = sc.nextLine();
        System.out.print("Enter activity category id:");
        activityCategoryId = sc.nextLine();
        System.out.print("Enter number of points for this activity:");
        numOfPoints = sc.nextLine();

        int enteredValue = Helper.selectNextOption(sc, "Update RERule");
        CallableStatement statement = null;

        if (enteredValue == 2) {
            brandPage();
        } else {
            try {
                //TODO: Change query as per our tables
                statement = MainMenu.connection.prepareCall("{call update_rerule(?, ?, ?, ?)}");
                statement.setString(1, rerCode);
                statement.setString(2, activityCategoryId);
                statement.setString(3, numOfPoints);
                statement.registerOutParameter(4, Types.INTEGER);

                statement.execute();

                int result = statement.getInt(4);

                statement.close();

                if (result == 0) {
                    System.out.println("RERule with this code is not present.");
                } else {
                    System.out.println("RERule has been updated successfully.");
                }
                updateRERule();
            } catch(SQLException e)
            {
                Helper.close(statement);
                System.out.println("RERule could not be updated. Please try again.");
                updateRERule();
            }
        }
    }

    public static void addRRRule() {
        String rrrCode, rewardCategoryId, numOfPoints;

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Reward Redemption Rule code:");
        rerCode = sc.nextLine();
        System.out.print("Enter reward category id:");
        activityCategoryId = sc.nextLine();
        System.out.print("Enter number of points for this reward:");
        numOfPoints = sc.nextLine();

        int enteredValue = Helper.selectNextOption(sc, "Add RRRule");
        CallableStatement statement = null;

        if (enteredValue == 2) {
            brandPage();
        } else {
            try {
                //TODO: Change query as per our tables
                statement = MainMenu.connection.prepareCall("{call add_rrrule(?, ?, ?, ?)}");
                statement.setString(1, rrrCode);
                statement.setString(2, rewardCategoryId);
                statement.setString(3, numOfPoints);
                statement.registerOutParameter(4, Types.INTEGER);

                statement.execute();

                int result = statement.getInt(4);

                statement.close();

                if (result == 0) {
                    System.out.println("RRRule with this code already present.");
                } else if(ret == 1) {
                    System.out.println("RRRule added successfully.");
                } else {
                    System.out.println("RRRule could not be added. Please try again.");
                }
                addRRRule();
            } catch(SQLException e)
            {
                Helper.close(statement);
                System.out.println("RRRule could not be added. Please try again.");
                addRRRule();
            }
        }
    }

    public static void updateRRRule() {
        String rrrCode, rewardCategoryId, numOfPoints;

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter code of Reward Redemption Rule that you want to update:");
        rrrCode = sc.nextLine();
        System.out.print("Enter reward category id:");
        rewardCategoryId = sc.nextLine();
        System.out.print("Enter number of points for this reward:");
        numOfPoints = sc.nextLine();

        int enteredValue = Helper.selectNextOption(sc, "Update RRRule");
        CallableStatement statement = null;

        if (enteredValue == 2) {
            brandPage();
        } else {
            try {
                //TODO: Change query as per our tables
                statement = MainMenu.connection.prepareCall("{call update_rrrule(?, ?, ?, ?)}");
                statement.setString(1, rrrCode);
                statement.setString(2, rewardCategoryId);
                statement.setString(3, numOfPoints);
                statement.registerOutParameter(4, Types.INTEGER);

                statement.execute();

                int result = statement.getInt(4);

                statement.close();

                if (result == 0) {
                    System.out.println("RRRule with this code is not present.");
                } else {
                    System.out.println("RRRule has been updated successfully.");
                }
                updateRRRule();
            } catch(SQLException e)
            {
                Helper.close(statement);
                System.out.println("RRRule could not be updated. Please try again.");
                updateRRRule();
            }
        }
    }
    
    public static void validateLoyaltyProgram() {
        CallableStatement statement = null;
        try {
            statement = Home.connection.prepareCall("{call validate_loyalty_program(?, ?, ?, ?)}");
            statement.setString(1, Login.loggedInUserId);
            statement.setString(2, lpName);
            statement.setString(3, lpType);
            statement.registerOutParameter(4, Types.INTEGER);

            statement.execute();

            int ret = statement.getInt(4);

            if (ret == 0) {
                System.out.println("Tiers are not defined. Please define the Tiers.");
            } else if (ret == 1) {
                System.out.println("One Reward Earning rule must be defined.");
            } else if (ret == 2) {
                System.out.println("One Reward Redeeming rule must be defined.");
            } else {
                System.out.println("Loyalty Program has been validated and set to active status.");
                isActive = true;
            }

            statement.close();

        } catch (SQLException e) {
            Utility.close(statement);
            System.out.println("Loyalty Program can not be validated. Please try again.");
        }
    }

    private static void intialize() {
        String sql = "select BRAND_LP_ID, TYPE, STATE from LOYALTY_PROGRAM where BRAND_LP_ID =  '" + Login.loggedInUserId + "'";

        ResultSet rs = null;
        try {
            rs = MainMenu.statement.executeQuery(sql);
            if (rs.next()) {
                isEnrolled = true;
                lpName = rs.getString("BRAND_LP_ID");
                isActive = rs.getBoolean("STATE");
                lpType = rs.getString("TYPE");
            }

            rs.close();
        } catch (SQLException e) {
            Helper.close(rs);
            e.printStackTrace();
        }
    }
}