import java.util.Scanner;

public class Brand {
    public static void brandPage() {
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
                statement = MainMenu.connection.prepareCall("{call add_rerule(?, ?, ?, ?)}");
                statement.setString(1, rerCode);
                statement.setString(2, activityCategoryId);
                statement.setString(3, numOfPoints);
                statement.registerOutParameter(4, Types.INTEGER);

                statement.execute();

                int result = statement.getInt(4);

                statement.close();

                if (ret == 0) {
                    System.out.println("RERule with this code already present.");
                } else if(ret == 1) {
                    System.out.println("RERule added successfully.");
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
        
    }
}