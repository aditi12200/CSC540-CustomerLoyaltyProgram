import java.util.Scanner;


public class Regular {
    public static void regularPage() {
        Scanner sc = new Scanner(System.in);
        int enteredValue;
        boolean selected = false;

        do {
            System.out.println("Choose what operation you want to perform");
            System.out.println("1. Activity Types");
            System.out.println("2. Reward Types");
            System.out.println("3. Go Back");
            System.out.print("Enter your option:");

            try {
                enteredValue = sc.nextInt();
                selected = true;

                switch (enteredValue) {
                    case 1:
                        ActivityType.activityTypePage();
                        break;
                    case 2:
                        RewardType.rewardTypePage();
                        break;
                    case 3:
                        LoyaltyProgram.loyaltyPage();
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
}