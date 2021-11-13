import oracle.jdbc.internal.OracleConnection;

import java.util.Scanner;
import java.util.*;
import java.sql.*;

public class Customer {
    public static void customerPage() {
        Scanner sc = new Scanner(System.in);
        int enteredValue;
        boolean selected = false;

        do {
            System.out.println("Choose one of the following options");
            System.out.println("1. Enroll in Loyalty Program");
            System.out.println("2. Reward Activities");
            System.out.println("3. View Wallet");
            System.out.println("4. Redeem Points");
            System.out.println("5. Log out");

            try {
                enteredValue = sc.nextInt();
                selected = true;

                switch (enteredValue) {
                    case 1:
                        enrollLoyaltyProgram();
                        break;
                    case 2:
                        performRewardActivities();
                        break;
                    case 3:
                        viewWallet();
                        break;
                    case 4:
                        redeemPoints();
                        break;
                    case 5:
                        MainMenu.displayMenu();
                        break;
                    default:
                        System.out.println("You have made an invalid choice. Please pick again.");
                        selected = false;
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Please pick an option between 1 and 5.");
                customerPage();
            }
        } while (!selected);
    }

    public static void enrollLoyaltyProgram() {
        String chosenLoyaltyProgram;
        Scanner sc=new Scanner(System.in);
        List<String> availableLoyaltyPrograms = new ArrayList<String>();
        List<String> availableLoyaltyProgramIds = new ArrayList<String>();

        int enteredValue = 0;

            try {
                String sqlLoyaltyProgramSelect = "select * from LOYALTY_PROGRAM L, BRAND B where L.BRAND_LP_ID=B.BRAND_ID and L.STATE='ACTIVE'";
                ResultSet rs = MainMenu.statement.executeQuery(sqlLoyaltyProgramSelect);
                while (rs.next()) {
                    String loyaltyProgram = rs.getString("NAME");
                    String brandIds = rs.getString("BRAND_ID");
                    availableLoyaltyPrograms.add(loyaltyProgram);
                    availableLoyaltyProgramIds.add(brandIds);
                }
            } catch (SQLException e) {
                System.out.println("No active loyalty programs at the moment.");
                customerPage();
            }

            boolean correctValue = false;
            boolean customerIsEnrolled = true;
            String LPId="";
            int LP_index;
            while (!correctValue && customerIsEnrolled) {
                if(availableLoyaltyPrograms.size()==0 || availableLoyaltyProgramIds.size()==0){
                    System.out.println("No available loyalty programs at the moment.");
                    Customer.customerPage();
                }
                else{
                    System.out.println("List of available loyalty programs: ");

                    for (String prog : availableLoyaltyPrograms) {
                        System.out.println(prog);
                    }


                    System.out.println("Enter the loyalty program you want to enroll in: ");
                    chosenLoyaltyProgram = sc.nextLine();

                    correctValue = availableLoyaltyPrograms.contains(chosenLoyaltyProgram);
                    if (correctValue){
                        enteredValue = Helper.selectNextOption(sc, "Enroll in Loyalty Program");
                        if(enteredValue==2){
                            Customer.customerPage();
                        }

                        LP_index = availableLoyaltyPrograms.indexOf(chosenLoyaltyProgram);
                        LPId = availableLoyaltyProgramIds.get(LP_index);

                        customerIsEnrolled = checkIfCustomerEnrolled(LPId);

                        if (customerIsEnrolled) {
                            System.out.println("You are already enrolled in the loyalty program.");
                            Customer.customerPage();
                        }
                    }
                    else {
                        System.out.println("Chosen loyalty program doesn't exist. Choose again.");
                    }

                }
            }
            //customer has chosen a new and correct loyalty program --> chosenLoyaltyProgram
            //Entry into wallet table assuming wallet_id is auto_generated
            String loyaltyProgramType="";
            String tierStatus="";
            String joinCategoryCode="";
            int walletId=0;
            int activityId=0;
            try {
                String sqlLPTypeSelect = "select * from LOYALTY_PROGRAM where BRAND_LP_ID='" + LPId +"'";
                ResultSet rs1 = MainMenu.statement.executeQuery(sqlLPTypeSelect);
                PreparedStatement ps;
                if (rs1.next()) {
                    loyaltyProgramType = rs1.getString("TYPE");
                } else{
                    System.out.println("No Loyalty program found");
                    enrollLoyaltyProgram();
                }

                if ((loyaltyProgramType.toLowerCase()).equals("r")) {
                    try {
                        ps = MainMenu.connection.prepareStatement("Insert into WALLET (BRAND_ID, CUST_ID,POINTS, CUMULATIVE_PTS) values (?,?,?,?)");
                        ps.setString(1, LPId);//name
                        ps.setString(2, Login.userId);
                        ps.setInt(3, 0);
                        ps.setInt(4,0);

                        int row=ps.executeUpdate();
                        if(row<=0){
                            System.out.println("Could not create a wallet");
                            enrollLoyaltyProgram();
                        }
                    } catch (SQLIntegrityConstraintViolationException e) {
                        System.out.println("Could not create a wallet");
                        enrollLoyaltyProgram();
                    }

                } else if ((loyaltyProgramType.toLowerCase()).equals("t")) {
                    try {
                        String sqlTierSelect = "select * from TIER where BRAND_ID='" + LPId + "' and PRECEDENCE=1";
                        ResultSet rs2 = MainMenu.statement.executeQuery(sqlTierSelect);
                        if (rs2.next()) {
                            tierStatus = rs2.getString("TIER_NAME");
                        } else{
                            System.out.println("No tier Name found");
                            enrollLoyaltyProgram();
                        }
                        ps = MainMenu.connection.prepareStatement("Insert into WALLET (BRAND_ID, CUST_ID, POINTS, CUMULATIVE_PTS, TIER_STATUS) values (?,?,?,?,?)");
                        ps.setString(1, LPId);
                        ps.setString(2, Login.userId);
                        ps.setInt(3, 0);
                        ps.setInt(4,0);
                        ps.setString(5, tierStatus);
                        int row=ps.executeUpdate();
                        if(row==0){
                            System.out.println("Could not create a wallet");
                            enrollLoyaltyProgram();
                        }
                    } catch (SQLIntegrityConstraintViolationException e) {
                        e.printStackTrace();
                        System.out.println("Wallet already exists");
                        enrollLoyaltyProgram();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("Could not create a wallet");
                        enrollLoyaltyProgram();
                    }
                }
                //find join activity category code
                try {
                    String sqlActCategorySelect = "select AT_ID from ACTIVITY_TYPE where ACTIVITY_NAME='JOIN'";
                    ResultSet rs3 = MainMenu.statement.executeQuery(sqlActCategorySelect);
                    if (rs3.next()) {
                        joinCategoryCode = rs3.getString("AT_ID");
                    }
                    //entry into activity table
                    ps = MainMenu.connection.prepareStatement("Insert into ACTIVITY (ACT_DATE, ACT_CATEGORY_CODE, VALUE) values (?,?,?)");
                    ps.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now()));
                    ps.setString(2, joinCategoryCode);
                    ps.setString(3, "JOIN");
                    int row=ps.executeUpdate();
                    if(row<=0){
                        System.out.println("Could not record activity");
                        enrollLoyaltyProgram();
                    }
                } catch (SQLIntegrityConstraintViolationException e) {
                    System.out.println("Integrity Constraint Violation while inserting in activity table");
                    enrollLoyaltyProgram();
                }catch (SQLException e) {
                    System.out.println("Could not record activity");
                    enrollLoyaltyProgram();
                }

                //find wallet_id of customer for current brand
                try {
                    String walletIdSelect = "select MAX(WALLET_ID) AS MAX_WALLET_ID from WALLET where CUST_ID='" + Login.userId+"'";
                    ResultSet rs4 = MainMenu.statement.executeQuery(walletIdSelect);

                    if (rs4.next()) {
                        walletId = rs4.getInt("MAX_WALLET_ID");
                    } else{
                        System.out.println("Wallet does not exist");
                        enrollLoyaltyProgram();
                    }
                } catch (SQLException e) {
                    System.out.println("Could not fetch wallet details");
                    enrollLoyaltyProgram();
                }


                //find activity_id from activity table
                try {
                    String activityIdSelect = "select MAX(ACT_ID) AS MAX_ACT_ID from ACTIVITY";
                    ResultSet rs4 = MainMenu.statement.executeQuery(activityIdSelect);

                    if (rs4.next()) {
                        activityId = rs4.getInt("MAX_ACT_ID");
                    } else {
                        System.out.println("Activity not found");
                        enrollLoyaltyProgram();
                    }
                } catch (SQLException e) {
                    System.out.println("Could not fetch activity details");
                    enrollLoyaltyProgram();
                }
                //entry into wallet_acitivity_bridgetable
                try {
                    ps = MainMenu.connection.prepareStatement("Insert into WALLET_ACTIVITY(WALLET_ID, ACT_ID) values (?,?)");
                    ps.setInt(1, walletId);
                    ps.setInt(2, activityId);
                    int row=ps.executeUpdate();
                    if(row<=0){
                        System.out.println("Could not record wallet and activity");
                        enrollLoyaltyProgram();
                    }
                } catch (SQLIntegrityConstraintViolationException e) {
                    System.out.println("Integrity Constraint Violation while inserting in wallet activity table");
                    enrollLoyaltyProgram();
                } catch(SQLException e) {
                    System.out.println("Could not record wallet and activity");
                    enrollLoyaltyProgram();
                }

                System.out.println("Successfully enrolled in Loyalty Program");
                customerPage();

            } catch (SQLException e) {
                System.out.println("SQL Exception Encountered"); //figure out this message
            }
    }

    private static boolean checkIfCustomerEnrolled(String chosenLP){
        String sqlWalletSelect="select * from WALLET where CUST_ID='"+Login.userId+"'and BRAND_ID='"+chosenLP+"'";
        try {
            ResultSet rs = MainMenu.statement.executeQuery(sqlWalletSelect);

            if(rs.next()) {
                return true;
            }
            return false;
        } catch(SQLException e) {
            System.out.println("Could not determine if customer is already enrolled.");
            enrollLoyaltyProgram();
        }
        return true;
    }

    public static void viewWallet() {
        String customerId = Login.userId;
        Scanner sc = new Scanner(System.in);
        int enteredValue=1;
        do {
            if (enteredValue == 2) {
                customerPage();
            } else {
                try {
                    String walletSelect = "select * from WALLET where CUST_ID='" + customerId +"'";
                    ResultSet rs = MainMenu.statement.executeQuery(walletSelect);
                    System.out.println("Wallet ID\tBrand ID\tCustomer ID\tPoints\tCumulative_pts\tTier Status");
                    while (rs.next()) {
                        int walletId = rs.getInt("WALLET_ID");
                        String brandId = rs.getString("BRAND_ID");
                        String custId = rs.getString("CUST_ID");
                        int points = rs.getInt("POINTS");
//                        int sumPts = rs.getInt("CUMULATIVE_PTS");
                        String tierStatus = rs.getString("TIER_STATUS");

                        System.out.println(walletId + "\t" + brandId + "\t" + custId + "\t" + points + "\t" + tierStatus);
                    }
                } catch (SQLException e) {
                    System.out.println("You do not have any existing wallets.");
                }
                enteredValue = Helper.selectNextOption(sc, "View Wallet");
            }
        } while (enteredValue != 2);
    }
    public static Map<String, String> fetchBrands() {
        ResultSet rs;
        Scanner sc = new Scanner(System.in);
        Map<String, String> brands = new HashMap<String, String>();
        System.out.println("The Brands in Loyalty Program in which you have enrolled are:");
        try
        {
            String SQL_Wallet = "Select B.NAME, B.BRAND_ID " +
                    "from BRAND B, WALLET W " +
                    "where B.BRAND_ID = W.BRAND_ID AND W.CUST_ID = '"+Login.userId+"'";
            rs = MainMenu.statement.executeQuery(SQL_Wallet);
            while(rs.next())
            {
                brands.put(rs.getString("BRAND_ID"), rs.getString("NAME"));
            }
        }
        catch(SQLException e)
        {
            System.out.println("Could not fetch the brands you are enrolled in");
        }
        return brands;
    }
    public static Map<Integer,String> getRewardActForBrand(String brandId) {
        int i=1;
        ResultSet rs;
        Map<Integer,String> rewardActCategories = new HashMap<Integer,String>();
        try
        {
            String SQL_Activity_name = "SELECT A.ACTIVITY_NAME " +
                    "FROM ACTIVITY_TYPE A, LP_ACT_CATEGORY L " +
                    "WHERE L.ACT_CATEGORY_CODE = A.AT_ID AND BRAND_ID = '"+brandId+"'";
            rs = MainMenu.statement.executeQuery(SQL_Activity_name);
            while(rs.next()) {
                rewardActCategories.put(i++, rs.getString("ACTIVITY_NAME"));
            }
        }
        catch(SQLException e)
        {
            System.out.println("Activity Names could not be fetched. Please try again.");
        }
        return rewardActCategories;
    }

    public static List<Integer> getGiftCardForCustomer(String brandId){
        List<Integer> giftCards=new ArrayList<Integer>();
        ResultSet rs;
        try
        {
            String SQL_Activity_code = "SELECT GIFT_CARD_CODE " +
                    "FROM WALLET W, WALLET_GIFTCARD WG " +
                    "WHERE W.WALLET_ID=WG.WALLET_ID AND W.BRAND_ID= '"+brandId+"' AND W.CUST_ID='"+Login.userId+"'";
            rs = MainMenu.statement.executeQuery(SQL_Activity_code);


            // for that ACC and brand_id, find number of points from RER table
            while(rs.next())
            {
                giftCards.add(rs.getInt("GIFT_CARD_CODE"));
            }
        }
        catch(SQLException e)
        {
            System.out.println("Gift card could not be retrieved. Please try again.");
        }

        return giftCards;
    }

    public static void deleteGiftCard(int gcc){
        ResultSet rs;
        String deleteGcSql = "DELETE from WALLET_GIFTCARD where GIFT_CARD_CODE=?";
        try {
            PreparedStatement ps = MainMenu.connection.prepareStatement(deleteGcSql);
            ps.setInt(1, gcc);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Gift card could not be updated as used, no rows affected.");
            }
        } catch (SQLException e) {
            System.out.println("Gift card could not be used. Please try again.");
            performRewardActivities();
        }
    }

    public static String getAccCode(String value_option){
        String acc = "";
        ResultSet rs;
        try
        {
            String SQL_Activity_code = "SELECT AT_ID " +
                    "FROM ACTIVITY_TYPE" +
                    " WHERE ACTIVITY_NAME = '"+value_option+"'";
            rs = MainMenu.statement.executeQuery(SQL_Activity_code);
            // for that ACC and brand_id, find number of points from RER table
            if(rs.next())
            {
                acc = rs.getString("AT_ID");
            }

        }
        catch(SQLException e)
        {   e.printStackTrace();
            System.out.println("Act category could not be fetched.");
            performRewardActivities();
        }
        return acc;
    }

    public static int getPoints(String acc, String brandId){
        ResultSet rs;
        int points = 0;
        try
        {
            String SQL_RER_points = "SELECT POINTS FROM RE_RULES" +
                    " WHERE ACT_CATEGORY_CODE = '"+acc+"' AND BRAND_ID = '"+brandId+"' AND VERSION_NO = (SELECT MAX(VERSION_NO)" +
                    "FROM RE_RULES" +
                    " WHERE ACT_CATEGORY_CODE = '"+acc+"' AND BRAND_ID = '"+brandId+"')";
            rs = MainMenu.statement.executeQuery(SQL_RER_points);
            if(rs.next())
            {
                points = rs.getInt("POINTS");
            }

        }
        catch(SQLException e)
        {
            System.out.println("Points could not be fetched.");
            performRewardActivities();
        }
        return points;
    }

    public static String getLpType(String brandId){
        String type = "";
        ResultSet rs;
        try
        {
            String SQL_RER_points = "SELECT TYPE " +
                    "FROM LOYALTY_PROGRAM" +
                    " WHERE BRAND_LP_ID = '"+brandId+"'";
            rs = MainMenu.statement.executeQuery(SQL_RER_points);
            if(rs.next())
            {
                type = rs.getString("TYPE");
            }
        }
        catch(SQLException e)
        {
            System.out.println("Could not fetch Loyalty Program type.");
            performRewardActivities();
        }
        return type;
    }

    public static String getTierStatus(String brandId){
        String tier_status = "";
        ResultSet rs;
        try
        {
            String SQL_RER_points = "SELECT TIER_STATUS " +
                    "FROM WALLET" +
                    " WHERE BRAND_ID = '"+brandId+"' AND CUST_ID = '"+Login.userId+"'";
            rs = MainMenu.statement.executeQuery(SQL_RER_points);
            if(rs.next())
            {
                tier_status = rs.getString("TIER_STATUS");
            }
        }
        catch(SQLException e)
        {
            System.out.println("Tier Status could not be fetched");
            performRewardActivities();
        }
        return tier_status;
    }

    public static int getMultiplier(String brandId, String tier_status){
        int multiplier = 0;
        ResultSet rs;
        try
        {
            String SQL_RER_points = "SELECT MULTIPLIER" +
                    " FROM TIER" +
                    " WHERE BRAND_ID = '"+brandId+"' AND TIER_NAME = '"+tier_status+"'";
            rs = MainMenu.statement.executeQuery(SQL_RER_points);
            if(rs.next())
            {
                multiplier = rs.getInt("MULTIPLIER");
            }
        }
        catch(SQLException e)
        {
            System.out.println("Multiplier could not be fetched");
            performRewardActivities();
        }
        return multiplier;
    }

    public static void updateWallet(int Total, int Sumtotal, String brandId){
        ResultSet rs;
        try
        {
            PreparedStatement ps = MainMenu.connection.prepareStatement("UPDATE WALLET" +
                    " SET POINTS = " +Total+ ", CUMULATIVE_PTS = "+Sumtotal+
                    " WHERE CUST_ID = '"+Login.userId+"' AND BRAND_ID = '"+brandId+"'");
            ps.executeUpdate();
        }
        catch (SQLIntegrityConstraintViolationException e)
        {
            System.out.println("Points cannot be updated, Please try again. " + e);
            performRewardActivities();
        }
        catch(SQLException e)
        {
            System.out.println("Points cannot be updated, Please try again. " + e);
            performRewardActivities();
        }

    }

    public static void updateActivity(String acc, String activity_value) {
        ResultSet rs;
        try {
            PreparedStatement ps = MainMenu.connection.prepareStatement("Insert into ACTIVITY (ACT_DATE, ACT_CATEGORY_CODE, VALUE) values (?,?,?)");
            ps.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now()));
            ps.setString(2, acc);
            ps.setString(3, activity_value);
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Integrity Constraint Violation!");
            performRewardActivities();
        } catch (SQLException e) {
            System.out.println("Reward activity could not be recorded.");
            performRewardActivities();
        }
    }

    public static void updateWalletActivity(int walletId) {
        ResultSet rs;
        int activityId = 0;
        try {
            String activityIdSelect = "select MAX(ACT_ID) AS MAX_ACT_ID from ACTIVITY";
            ResultSet rs4 = MainMenu.statement.executeQuery(activityIdSelect);

            if (rs4.next()) {
                activityId = rs4.getInt("MAX_ACT_ID");
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception encountered");
            performRewardActivities();
        }
        //entry into wallet_acitivity_bridgetable
        try {
            PreparedStatement ps = MainMenu.connection.prepareStatement("Insert into WALLET_ACTIVITY(WALLET_ID, ACT_ID) values (?,?)");
            ps.setInt(1, walletId);
            ps.setInt(2, activityId);
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Integrity Constraint Violation");
            performRewardActivities();
        } catch (SQLException e) {
            System.out.println("Can not Insert into wallet and activity");
            performRewardActivities();
        }
    }

    public static void performRewardActivities()
    {
        ResultSet rs;
        Scanner sc = new Scanner(System.in);
        //PreparedStatement pstmt = null;
        Map<String, String> brands = new HashMap<String, String>();
        brands = fetchBrands();
        if(brands.isEmpty())
        {
            System.out.println("Please enroll in a brand to view it's activities.");
            customerPage();
        }

        boolean value = false;
        String selected_value="";
        while(!value)
        {
            System.out.println("List of Brand in which you have enrolled:");
            for(String item : brands.values())
            {
                System.out.println(item);
            }
            System.out.println("Select a Brand whose reward activities you want to see:");  //Check if input to be taken is the number
            selected_value = sc.nextLine();
            if(brands.containsValue(selected_value))
                value=true;
            if(!value)
            {
                System.out.println("Incorrect Brand selected. Please select from the list.");
            }
        }
        String brandId = "";
        //CODE TO FETCH REWARD ACTIVTIES FOR SELECTED BRAND
        for (Map.Entry<String, String> entry : brands.entrySet()) {
            if (entry.getValue().equals(selected_value)) {
                 brandId = entry.getKey();
                break;
            }
        }
        //SQL to get the reward activities for this brand -  get it by getting ids of supported reward activities from bridge table
        // and then get reward activity names from the reward category table.
        //TODO: Change this to fetch from RER table instead!
        //Assuming rs contains activity category names
        Map<Integer, String> rewardActCategories=new HashMap<Integer, String>();
        rewardActCategories = getRewardActForBrand(brandId);
        if(rewardActCategories.isEmpty()){
            System.out.println("No reward activities found.");
            customerPage();
        }
        int selected_option;
        boolean check = false;

        System.out.println("Select one of the option: ");
        for (Map.Entry<Integer, String> entry : rewardActCategories.entrySet()) {
            System.out.println(entry.getKey()+". "+entry.getValue());
            }
        selected_option = sc.nextInt();
        while(!check)
        {
            if (rewardActCategories.containsKey(selected_option))
            {
                check = true;
            }
            else
            {
                System.out.println("Invalid option. Please input the correct option.");
                selected_option = sc.nextInt();
            }
        }
        // get the string value for that particular integer
        String value_option = rewardActCategories.get(selected_option);
        int gcc=0;
        if(value_option.toLowerCase().equals("purchase")) {
            System.out.println("If you want to use a gift card, please enter the gift card code. If not, please press enter 0.");
            gcc=sc.nextInt();
            sc.nextLine();
        }
        // get activity_category_code from activity_category table
        System.out.println("Enter value for this activity");
        String activity_value=sc.nextLine();
        String acc;
        int points;
        String type;
        int wallet_points;
        int cumulative_points;
        String tier_status;
        int multiplier;
        int Total;
        int Sumtotal;

        if(gcc>0) {
            List<Integer> giftCards;
            giftCards=getGiftCardForCustomer(brandId);

            if(!giftCards.contains(gcc)) {
                System.out.println("Please select a valid gift card!");
                Customer.performRewardActivities();
            }
            deleteGiftCard(gcc);
        }

        acc = getAccCode(value_option);
        points = getPoints(acc,brandId);
        type = getLpType(brandId);
        int walletId;
        try
        {
            String SQL_RER_points = "SELECT WALLET_ID, POINTS, CUMULATIVE_PTS" +
                    "FROM WALLET" +
                    "WHERE BRAND_ID = '"+brandId+"' AND CUST_ID = '"+Login.userId+"'";
            rs = MainMenu.statement.executeQuery(SQL_RER_points);
            if(rs.next())
            {
                walletId=rs.getInt("WALLET_ID");
                wallet_points = rs.getInt("POINTS");
                cumulative_points = rs.getInt("CUMULATIVE_PTS");

                // if not a tiered program add into the wallet
                if (type.toLowerCase().equals('r'))
                {
                    Total = points + wallet_points;
                    Sumtotal = cumulative_points + Total;
                    updateWallet(Total,Sumtotal,brandId);
                    updateActivity(acc,activity_value);
                    updateWalletActivity(walletId);

                }
                // find out if its a tiered program, if so get the tier and the multiplier
                if (type.toLowerCase().equals('t'))
                {
                    //from wallet get the tier status
                    tier_status = getTierStatus(brandId);

                    //from tier table, brandid , tier_status, get multiplier
                    multiplier = getMultiplier(brandId,tier_status);

                    Total = wallet_points + multiplier * points;
                    Sumtotal = cumulative_points+Total;
                    checkForTierStatusUpgrade(Sumtotal,brandId,tier_status,Login.userId);
                    updateWallet(Total,Sumtotal,brandId);
                    updateActivity(acc,activity_value);
                    updateWalletActivity(walletId);
                }


            }


        }
        catch(SQLException e)
        {
            System.out.println("could not fetch Wallet Activities");
            performRewardActivities();
        }


    }

    public static void checkForTierStatusUpgrade(int Total, String brandId, String tier_status, String custId){
        String getTierPoints = "select TIER_NAME, POINTS from TIER where BRAND_ID ='"+brandId+"'";
        ResultSet rs;
        Map<Integer, String> tiername_pts = new TreeMap<Integer, String>(Collections.reverseOrder());
        try
        {
            rs = MainMenu.statement.executeQuery(getTierPoints);
            while(rs.next()) {
                tiername_pts.put(rs.getInt("POINTS"),rs.getString("TIER_NAME"));
            }
            String maxTier = "";
            for(Map.Entry<Integer, String> entry : tiername_pts.entrySet())
            {
                if(entry.getKey()<=Total)
                {
                    maxTier = entry.getValue();
                }
            }
            if(!tier_status.equals(maxTier))
            {
                String update = "UPDATE WALLET SET TIER_STATUS='"+maxTier+"'" +
                        "where BRAND_ID='"+brandId+" and CUST_ID='"+custId+"'";
                PreparedStatement ps = MainMenu.connection.prepareStatement(update);
                ps.executeUpdate();
                System.out.println("Tier Status updated successfully");
            } else {
                System.out.println("Tier is already set");
            }
        } catch(SQLException e)
        {
            System.out.println("Tier status cannot be updated");
        }
    }

    public static void redeemPoints()
    {
        ResultSet rs;
        Scanner sc = new Scanner(System.in);
        //PreparedStatement pstmt = null;
        Map<String, String> brands_rp = new HashMap<String, String>();
        System.out.println("The Brands in Loyalty Program in which you have enrolled are:");
        try
        {
            String SQL_Wallet_rp = "SELECT B.NAME, B.BRAND_ID " +
                    "FROM BRAND B, WALLET W " +
                    "where B.BRAND_ID = W.BRAND_ID AND W.CUST_ID = '"+Login.userId+"'";
            rs = MainMenu.statement.executeQuery(SQL_Wallet_rp);
            while(rs.next())
            {
                brands_rp.put(rs.getString("BRAND_ID"), rs.getString("NAME"));
            }
        }
        catch(SQLException e)
        {
            System.out.println("Brands could not be fetched. Please try again.");
        }
        if(brands_rp.isEmpty())
        {
            System.out.println("Please enroll in a brand to redeem points.");
            customerPage();
        }

        boolean value = false;
        String selected_value = "";
        while(!value)
        {
            //System.out.println("List of Brand in which you have enrolled:");
            for(String item : brands_rp.values())
            {
                System.out.println(item);
            }
            System.out.println("Select a Brand whose reward activities you want to see:");  //Check if input to be taken is the number
            selected_value = sc.nextLine();
            if(brands_rp.containsValue(selected_value))
                value=true;
            if(!value)
            {
                System.out.println("Incorrect Brand selected. Please select from the list.");
            }
        }
        String brandId = null;
        //CODE TO FETCH REWARD ACTIVTIES FOR SELECTED BRAND
        for (Map.Entry<String, String> entry : brands_rp.entrySet()) {
            if (entry.getValue().equals(selected_value)) {
                brandId = entry.getKey();
                break;
            }
        }

        List<String> brands_rcc = new ArrayList<String>();
        try
        {
            String SQL_Wallet_rp = "SELECT REWARD_CATEGORY_CODE" +
                    " FROM REWARD" +
                    " WHERE BRAND_ID = '"+brandId+"' AND QUANTITY > 0";
            rs = MainMenu.statement.executeQuery(SQL_Wallet_rp);
            while(rs.next())
            {
                brands_rcc.add(rs.getString("REWARD_CATEGORY_CODE"));
            }
        }
        catch(SQLException e)
        {
            System.out.println("Reward categories could not be fetched. Please try again.");
        }
        if(brands_rcc.size() == 0)
        {
            System.out.println("Sorry! No rewards to redeem for this brand.");
            redeemPoints();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (String i : brands_rcc){
            sb.append(i+",");
        }
        sb.deleteCharAt(sb.length() -1);
        sb.append(")");
        String str = sb.toString();

        Map<String, Integer> brands_rrr = new HashMap<String, Integer>();
        //from the RRR table figure out which rewards are supported for this brand and display that
        try
        {
            String SQL_Wallet_rp = "SELECT R.REWARD_NAME, MAX(S.VERSION_NO)" +
                    " FROM REWARD_TYPE R, RR_RULES S" +
                    " WHERE S.BRAND_ID = '"+brandId+"' AND R.RT_ID = S.REWARD_CATEGORY_CODE AND R.RT_ID IN " + str +
                    " GROUP BY R.REWARD_NAME";
            rs = MainMenu.statement.executeQuery(SQL_Wallet_rp);
            while(rs.next())
            {
                brands_rrr.put(rs.getString("REWARD_NAME"), rs.getInt("VERSION_NO"));
            }
        }
        catch(SQLException e)
        {
            System.out.println("Reward Earning Rule could not be fetched. Please try again.");
            redeemPoints();
        }
        if(brands_rrr.size() == 0)
        {
            System.out.println("Sorry! No rewards to redeem.");
            redeemPoints();
        }


        value = false;
        String selected_reward = null;
        String R_C_C = null;
        int points = 0;
        //TODO: Identifier, Name
        while(!value)
        {
            System.out.println("List of rewards from which you can select:");
            for(String item : brands_rrr.keySet())
            {
                System.out.println(item);
            }
            System.out.println("Select a reward from the list:");
            selected_reward = sc.nextLine();
            if(brands_rrr.containsKey(selected_reward))
                value=true;
            if(!value)
            {
                System.out.println("Incorrect reward type selected. Please select from the list.");
            }
        }

        //CODE TO FETCH REWARD ACTIVTIES FOR SELECTED BRAND
        int version = 0;
        for (Map.Entry<String, Integer> entry : brands_rrr.entrySet()) {
            if (entry.getKey().equals(selected_reward)) {
                version = entry.getValue();
                break;
            }
        }

        try
        {
            String SQL_Reward = "SELECT S.POINTS, R.RT_ID " +
                    "FROM REWARD_TYPE R, RR_RULES S " +
                    "WHERE S.BRAND_ID = '"+brandId+"' AND R.RT_ID = S.REWARD_CATEGORY_CODE AND S.VERSION_NO = "+ version +
                    " AND R.REWARD_NAME = '"+selected_reward+"'";
            rs = MainMenu.statement.executeQuery(SQL_Reward);
            if(rs.next())
            {
                points = rs.getInt("POINTS");
                R_C_C = rs.getString("RT_ID");
            } else {
                System.out.println("Points for redeeming this reward could not be fetched.");
                redeemPoints();
            }
        }
        catch(SQLException e)
        {
            System.out.println("Points for redeeming this reward could not be fetched. Please try again.");
            redeemPoints();
        }

        int walletPts = 0;
        int walletId = 0;
        try
        {
            String SQL_WalletPts = "SELECT W.POINTS, W.WALLET_ID " +
                    "FROM WALLET W " +
                    "WHERE W.BRAND_ID = '"+brandId+"' AND W.CUST_ID = '"+Login.userId+"'";
            rs = MainMenu.statement.executeQuery(SQL_WalletPts);
            if(rs.next()) {
                walletPts = rs.getInt("POINTS");
                walletId = rs.getInt("WALLET_ID");
            } else {
                System.out.println("Wallet could not be found.");
                redeemPoints();
            }
        }
        catch(SQLException e)
        {
            System.out.println("Wallet could not be found. Please try again.");
            redeemPoints();
        }


        //if yes - deduct points from wallet, decrement no of instances by 1
        //update activity, wallet_activity tables. if GC update wallet_GC table
        if(walletPts>=points)
        {
            try
            {
                PreparedStatement ps = MainMenu.connection.prepareStatement("UPDATE WALLET " +
                        "SET POINTS = " +(walletPts-points)+
                        " WHERE WALLET_ID = "+walletId);
                ps.executeUpdate();
            }
            catch(SQLException e)
            {
                System.out.println("Wallet could not be updated");
                redeemPoints();
            }

            try
            {
                PreparedStatement ps = MainMenu.connection.prepareStatement("UPDATE REWARD " +
                        "SET QUANTITY = QUANTITY - 1 "+
                        "WHERE BRAND_ID = '"+brandId+"' AND REWARD_CATEGORY_CODE = '"+R_C_C+"' ");
                ps.executeUpdate();
            }
            catch(SQLException e)
            {
                System.out.println("Reward Quantity could not be updated");
                redeemPoints();
            }

            String redeemCategoryCode = null;
            try {
                String sqlActCategorySelect = "select AT_ID from ACTIVITY_TYPE where ACTIVITY_NAME='REDEEM'";
                ResultSet rs3 = MainMenu.statement.executeQuery(sqlActCategorySelect);
                if (rs3.next()) {
                    redeemCategoryCode = rs3.getString("AT_ID");
                }
                //entry into activity table
                PreparedStatement ps = MainMenu.connection.prepareStatement("Insert into ACTIVITY (ACT_DATE, ACT_CATEGORY_CODE, VALUE) values (?,?,?)");
                ps.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now()));
                ps.setString(2, redeemCategoryCode);
                ps.setString(3, R_C_C);
                ps.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException e) {
                e.printStackTrace();
                System.out.println("Integrity Constraint Violation in Activity Table");
                redeemPoints();
            } catch (SQLException e) {
                System.out.println("Activity could not be logged into Activity Table");
                redeemPoints();
            }

            int activityId = 0;
            try {
                String activityIdSelect = "select MAX(ACT_ID) AS MAX_ACT_ID from ACTIVITY";
                ResultSet rs4 = MainMenu.statement.executeQuery(activityIdSelect);

                if (rs4.next()) {
                    activityId = rs4.getInt("MAX_ACT_ID");
                }
            } catch (SQLException e) {
                System.out.println("SQL Exception encountered while fetching latest redeem activity");
                redeemPoints();
            }
            //entry into wallet_acitivity_bridgetable
            try {
                PreparedStatement ps = MainMenu.connection.prepareStatement("Insert into WALLET_ACTIVITY(WALLET_ID, ACT_ID) values (?,?)");
                ps.setInt(1, walletId);
                ps.setInt(2, activityId);
                ps.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException e) {
                System.out.println("Integrity Constraint Violation in Wallet Activity Table");
                redeemPoints();
            } catch (SQLException e) {
                System.out.println("Could not assign activity to wallet");
                redeemPoints();
            }

            if(selected_reward.toLowerCase().equals("gift card"))
            {
                try {
                    PreparedStatement ps = MainMenu.connection.prepareStatement("Insert into WALLET_GIFTCARD(WALLET_ID) values (?)");
                    ps.setInt(1, walletId);
                    ps.executeUpdate();
                } catch (SQLIntegrityConstraintViolationException e) {
                    System.out.println("Integrity Constraint Violation in Wallet giftcard table");
                    redeemPoints();
                } catch(SQLException e) {
                    System.out.println("Could not assign a gift card");
                    redeemPoints();
                }
            }
        }
        else
        {
            System.out.println("You do not have enough points to redeem a reward");
            redeemPoints();
        }
    }
}