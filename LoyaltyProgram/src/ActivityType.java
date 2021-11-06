import java.util.Scanner;


public class ActivityType {
    public static void activityTypePage() {
        Scanner sc = new Scanner(System.in);
        int enteredValue;
        boolean goBack = false;
        Map<Integer, String> actCategories=new HashMap();

        initialize();

        do {
            System.out.println("Choose one of the following options");
            for (Map.Entry<String,String> entry : actCategories.entrySet()) {
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
        }
    }

    private static void addActivityType(String activityCategory) {
        String sqlActCatSelect = "Select AT_ID from ACTIVITY_TYPE WHERE ACTIVITY NAME='"+activityCategory+"'";
        String acc;
        try {
            ResultSet rs = MainMenu.statement.executeQuery(sqlActCatSelect);

            int rows = ps.executeQuery();
            if (rs.next()) {
                acc=rs.getString("AT_ID");
            } else {
                System.out.println("Activity Type could not be found. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("Activity Type could not be found. Please try again.");
        }

        String sql = "Insert into LP_ACT_CATEGORY(BRAND_ID, ACT_CATEGORY_CODE) values (?,?)";
        try {
            PreparedStatement ps = Home.connection.prepareStatement(sql);
            ps.setString(1, Login.userId);
            ps.setString(2, acc);

            int rows = ps.executeUpdate();
            if (rows == 1) {
                System.out.println("Activity Type has been added successfully.");
            } else {
                System.out.println("Activity Type could not be added. Please try again.");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Activity Type already present.");
        } catch (SQLException e) {
            System.out.println("Activity Type can not be added. Please try again.");
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
