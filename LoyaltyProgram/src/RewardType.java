import java.util.Scanner;


public class RewardType {
    public static void rewardTypePage() {
        Scanner sc = new Scanner(System.in);
        int enteredValue, quantity;
        boolean goBack = false;
        Map<Integer, String> rewardCategories=new HashMap();

        initialize();

        do {
            System.out.println("Enter quantity for chosen reward category");
            quantity=sc.nextInt();
            System.out.println("Choose one of the following options");
            for (Map.Entry<String,String> entry : rewardCategories.entrySet()) {
                System.out.println(entry.getKey() + ". " + entry.getValue());
            }
            int nextOptNum=rewardCategories.size()+1;
            System.out.println(nextOptNum+". Go Back");

            enteredValue=sc.nextInt();

            if(enteredValue==nextOptNum) {
                goBack=true;
                goBack();
            } else if(rewardCategories.containsKey(enteredValue)) {
                addRewardType(rewardCategories.get(enteredValue), quantity);
            } else {
                System.out.println("You have made an invalid choice. Please pick again.");
            }
        } while (!goBack);
    }

    private static void initialize() {
        try {
            String rewCategorySelectSql = "select REWARD_NAME from REWARD_TYPE";

            ResultSet rs = MainMenu.statement.executeQuery(rewCategorySelectSql);

            int i=1;
            while (rs.next()) {
                actCategories.put(i++, rs.getString("REWARD_NAME"));
            }
        } catch (SQLException e) {
            System.out.println("Reward categories could not be fetched. Please try again.");
        }
    }

    private static void addRewardType(String rewardCode, int quantity) {
        String sql = "Insert into REWARD(REWARD_CATEGORY_CODE, VALUE, QUANTITY, BRAND_ID) values (?,?,?,?)";
        try {
            PreparedStatement ps = Home.connection.prepareStatement(sql);
            ps.setString(1, Login.userId);
            ps.setString(2, activityCode);

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

    private static void goBack() {
        if (LoyaltyProgram.lpType.equalsIgnoreCase("R")) {
            Regular.regularPage();
        } else {
            Tier.tierPage();
        }
    }
}
