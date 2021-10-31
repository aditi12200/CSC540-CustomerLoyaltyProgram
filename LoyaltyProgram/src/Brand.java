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
                PreparedStatement ps = MainMenu.connection.prepareStatement("Insert into ActivityType (ACTIVITYCODE, ACTIVITYNAME) values (?,?,?)");
                ps.setString(1, rerCode);
                ps.setString(2, activityCategoryId);
                ps.setString(3, numOfPoints);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("RERule added successfully.");
                } else {
                    System.out.println("RERule could not be added. Please try again.");
                    addRERule
                }
            } catch (SQLIntegrityConstraintViolationException e) {
                System.out.println("RERule code already exists. Please try again.");
            } catch(SQLException e)
            {
                System.out.println("RERule Type could not be added. Please try again.");
                addRERule();
            }
        }
    }

    public static void updateRERule() {

    }

    public static void addRRRule() {

    }

    public static void updateRRRule() {

    }
    
    public static void validateLoyaltyProgram() {
        
    }
}