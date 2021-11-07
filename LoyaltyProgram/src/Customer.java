import java.util.Scanner;

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
                System.out.println("Please pick an option between 1 and 5.");
                sc.next();
            }
        } while (!selected);
    }

    public static void enrollLoyaltyProgram(){
        String chosenLoyaltyProgram;
        Scanner sc=new Scanner(System.in);
        List<String> availableLoyaltyPrograms = new ArrayList<String>();
        List<String> availableLoyaltyProgramIds = new ArrayList<String>();

        enteredValue = Helper.selectNextOption(sc, "enrollLoyaltyProgram");

        if (enteredValue==2){
            Customer.customerPage();
        }else {
            try {
                String sqlLoyaltyProgramSelect = "select * from LOYALTY_PROGRAM L, BRAND B where L.BRAND_ID=B.BRAND_ID and L.STATE='active'";
                ResultSet rs = MainMenu.statement.executeQuery(sqlLoyaltyProgramSelect);
                while (rs.next()) {
                    String loyaltyProgram = rs.getString("NAME");
                    String brandIds = rs.getString("BRAND_ID");
                    availableLoyaltyPrograms.add(loyaltyProgram);
                    availableLoyaltyProgramIds.add(brandIds);
                }
            } catch (SQLException e) {
                System.out.println("No active loyalty programs at the moment.")
            }

            boolean correctValue = false;
            boolean customerIsEnrolled = true;
            while (!correctValue && customerIsEnrolled) {
                System.out.println("List of available loyalty programs: ");

                for (String prog : availableLoyaltyPrograms) {
                    System.out.println(prog);
                }

                System.out.println("Enter the loyalty program you want to enroll in: ");
                chosenLoyaltyProgram = sc.nextLine();

                correctValue = availableLoyaltyPrograms.contains(chosenLoyaltyProgram);
                LP_index = availableLoyaltyPrograms.indexOf(chosenLoyaltyProgram);
                LPId = availableLoyaltyProgramIds.get(LP_index);

                customerIsEnrolled = checkIfCustomerEnrolled(LPId);
                if (!correctValue) {
                    System.out.println("Chosen loyalty program doesn't exist. Choose again.")
                }
                if (customerIsEnrolled) {
                    System.out.println("You are already enrolled in the loyalty program.");
                    Customer.customerPage();
                }
            }
            //customer has chosen a new and correct loyalty program --> chosenLoyaltyProgram
            //Entry into wallet table assuming wallet_id is auto_generated
            String loyaltyProgramType;
            String tierStatus;
            String joinCategoryCode;
            String walletId;
            String activityId;
            try {
                String sqlLPTypeSelect = "select * from LOYALTY_PROGRAM where BRAND_ID=" + LPId;//name
                ResultSet rs1 = MainMenu.statement.executeQuery(sqlLPTypeSelect);
                PreparedStatement ps;
                if (rs1.next()) {
                    loyaltyProgramType = rs1.getString("TYPE");
                }

                if (loyaltyProgramType.toLowerCase() == "R") {
                    try {
                        ps = MainMenu.connection.prepareStatement("Insert into WALLET (BRAND_ID, CUST_ID,POINTS) values (?,?,?)");
                        ps.setString(1, LPId);//name
                        ps.setString(2, Login.userId);
                        ps.setInt(3, 0);
                    } catch (SQLIntegrityConstraintViolationException e) {
                        System.out.println("Incorrect Brand ID");
                    }

                } else if (loyaltyProgramType.toLowerCase() == "T") {
                    try {
                        String sqlTierSelect = "select * from TIER where BRAND_ID=" + LPId + " and PRECEDENCE=1";
                        ResultSet rs2 = MainMenu.statement.executeQuery(sqlTierSelect);
                        if (rs2.next()) {
                            tierStatus = rs2.getString("TIER_NAME");
                        }
                        ps = MainMenu.connection.prepareStatement("Insert into WALLET (BRAND_ID, CUST_ID,POINTS,TIER_STATUS) values (?,?,?,?)");
                        ps.setString(1, LPId);
                        ps.setString(2, Login.userId);
                        ps.setInt(3, 0);
                        ps.setString(4, tierStatus)
                    } catch (SQLException e) {
                        System.out.println("Incorrect Brand ID");
                    } catch (SQLIntegrityConstraintViolation e) {
                        System.out.println("Incorrect Brand ID");
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
                } catch (SQLException e) {
                    System.out.println("SQL Exception encountered");
                } catch (SQLIntegrityConstraintViolation e) {
                    System.out.println("Integrity Constraint Violation");
                }

                //find wallet_id of customer for current brand
                try {
                    String walletIdSelect = "select MAX(WALLET_ID) AS MAX_WALLET_ID from WALLET where CUST_ID=" + Login.userId;
                    ResultSet rs4 = MainMenu.statement.executeQuery(walletIdSelect);

                    if (rs4.next()) {
                        walletId = rs4.getInt("WALLET_ID");
                    }
                } catch (SQLException e) {
                    System.out.println("SQL Exception encountered");
                }


                //find activity_id from activity table
                try {
                    String activityIdSelect = "select MAX(ACT_ID) AS MAX_ACT_ID from ACTIVITY;
                    rs4 = MainMenu.statement.executeQuery(activityIdSelect);

                    if (rs4.next()) {
                        activityId = rs4.getInt("MAX_ACT_ID");
                    }
                } catch (SQLException e) {
                    System.out.println("SQL Exception encountered");
                }
                //entry into wallet_acitivity_bridgetable
                try {
                    ps = MainMenu.connection.prepareStatement("Insert into WALLET_ACTIVITY(WALLET_ID, ACT_ID) values (?,?)");
                    ps.setString(1, walletId);
                    ps.setString(2, activityId);
                } catch (SQLIntegrityConstraintViolation e) {
                    System.out.println("Integrity Constraint Violation");
                }

            } catch (SQLException e) {
                System.out.println("SQL Exception Encountered"); //figure out this message
            }
        }
    }

    private static boolean checkIfCustomerEnrolled(String chosenLP){
        String sqlWalletSelect="select * from WALLET where CUST_ID="+Login.userId+" and BRAND_ID="+chosenLP;

        if(rs.next()) {
            return true;
        }
        return false;
    }

    public static void viewWallet() {
        String custId = Login.userId;
        Scanner sc = new Scanner(System.in);
        int enteredValue;
        do {
            enteredValue = Helper.selectNextOption(sc, "viewWallet");

            if (enteredValue == 2) {
                customerPage();
            } else {
                try {
                    String walletSelect = "select * from WALLET where WALLET.CUST_ID=" + custId;
                    ResultSet rs = MainMenu.statement.executeQuery(walletSelect);
                    System.out.println("Wallet ID\tBrand ID\tCustomer ID\tPoints\tTier Status");
                    while (rs.next()) {
                        String walletId = rs.getString("WALLET_ID");
                        String brandId = rs.getString("BRAND_ID");
                        String custId = rs.getString("CUST_ID");
                        int points = rs.getInt("POINTS");
                        String tierStatus = rs.getString("TIER_STATUS");

                        System.out.println(walletId + "\t" + brandId + "\t" + custId + "\t" + points + "\t" + tierStatus);
                    }
                } catch (SQLException e) {
                    System.out.println("You do not have any existing wallets.");
                }
            }
        } while (enteredValue != 2)
    }

    public static void performRewardActivities()
    {
        ResultSet rs;
        Scanner sc = new Scanner(System.in);
        //PreparedStatement pstmt = null;
        Map<String, String> brands = new HashMap();
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
            System.out.println("Activity Type can not be selected, Please try again.");
        }

        if(brands.isEmpty())
        {
            System.out.println("Please enroll in a brand to view it's activities.");
            customerPage();
        }

        boolean value = false;
        String selected_value;
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
        //CODE TO FETCH REWARD ACTIVTIES FOR SELECTED BRAND
        for (Entry<String, String> entry : brands.entrySet()) {
            if (entry.getValue().equals(selected_value)) {
                String brandId = entry.getKey();
                break;
            }
        }
        //SQL to get the reward activities for this brand -  get it by getting ids of supported reward activities from bridge table
        // and then get reward activity names from the reward category table.
        //TODO: Change this to fetch from RER table instead!
        try
        {
            String SQL_Activity_name = "SELECT A.ACTIVITY_NAME " +
                    "FROM ACTIVITY_TYPE A, LP_ACT_CATEGORY L" +
                    "WHERE L.ACT_CATEGORY_CODE = A.AT_ID AND BRAND_ID = '"+brandId+"'";
            rs = MainMenu.statement.executeQuery(SQL_Activity_name);
        }
        catch(SQLException e)
        {
            System.out.println("Activity Type can not be added, Please try again.");
        }

        //Assuming rs contains activity category names
        Map<Integer, String> rewardActCategories=new HashMap();
        int i=1;
        int selected_option;
        boolean check = false;
        while(rs.next()) {
            rewardActCategories.put(i++, rs.getString("ACTIVITY_NAME"));
        }
        System.out.println("Select one of the option: ");
        for (Entry<String, String> entry : rewardActCategories.entrySet()) {
            System.out.println(entry.getKey(), ". ", entry.getValue());
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
        String gcc="";
        if(value_option.toLowerCase()=="purchase") {
            System.out.println("If you want to use a gift card, please enter the gift card code. If not, please press enter.");
            gcc=sc.nextLine();
            gcc=gcc.trim();
        }
        // get activity_category_code from activity_category table
        System.out.println("Enter value for this activity");
        String activity_value=sc.nextLine();
        int acc;
        int points;
        String type;
        int wallet_points;
        String tier_status;
        int multiplier;
        String Total;

        if(gcc.length()>0) {
            try
            {
                String SQL_Activity_code = "SELECT GIFT_CARD_CODE " +
                        "FROM WALLET W, WALLET_GIFTCARD WG " +
                        "WHERE W.WALLET_ID=WG.WALLET_ID AND W.BRAND_ID= '"+brandId+"' AND W.CUST_ID='"+Login.userId+"'";
                ResultSet rs = MainMenu.statement.executeQuery(SQL_Activity_code);
            }
            catch(SQLException e)
            {
                System.out.println("Gift card could not be retrieved. Please try again.");
            }

            List<String> giftCards=new ArrayList();
            // for that ACC and brand_id, find number of points from RER table
            while(rs.next())
            {
                giftCards.add(rs.getInt("GIFT_CARD_CODE"));
            }

            if(!giftCards.contains(gcc)) {
                System.out.println("Please select a valid gift card!");
                performRewardActivties();
            }

            String deleteGcSql = "DELETE from WALLET_GIFTCARD where GIFT_CARD_CODE=?";
            try {
                PreparedStatement ps = Home.connection.prepareStatement(deleteGcSql);
                ps.setString(1, gcc);

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    throw new SQLException("Gift card could not be updated as used, no rows affected.");
                }
            } catch (SQLException e) {
                System.out.println("Gift card could not be used. Please try again.");
                performRewardActivities();
            }

            //TODO: CODE TO CHECK IF PURCHASE AMOUNT IS LESS THAN OR EQUAL TO GC AMT. IF YES, SET GiVEREWARD FLAG TO FALSE?

        }

        try
        {
            String SQL_Activity_code = "SELECT ACT_CATEGORY_CODE " +
                    "FROM ACTIVITY_TYPE" +
                    "WHERE ACTIVITY_NAME = '"+value_option+"'";
            rs = MainMenu.statement.executeQuery(SQL_Activity_code);
        }
        catch(SQLException e)
        {
            System.out.println("Activity Type can not be added, Please try again.");
        }

        // for that ACC and brand_id, find number of points from RER table
        if(rs.next())
        {
            acc = rs.getInt("ACT_CATEGORY_CODE");
        }

        int versionNo;
        try
        {
            String SQL_RER_latestVersion = "SELECT MAX(VERSION_NO) AS MAX_VERSION" +
                    "FROM RE_RULES" +
                    "WHERE ACT_CATEGORY_CODE = '"+acc+"' AND BRAND_ID = '"+brandId+"'";
            rs = MainMenu.statement.executeQuery(SQL_RER_points);
        }
        catch(SQLException e)
        {
            System.out.println("Activity Type can not be added, Please try again.");
        }
        if(rs.next())
        {
            versionNo = rs.getInt("MAX_VERSION");
        }

        try
        {
            String SQL_RER_points = "SELECT POINTS " +
                    "FROM RE_RULES" +
                    "WHERE ACT_CATEGORY_CODE = '"+acc+"' AND BRAND_ID = '"+brandId+"' AND VERSION_NO = '"+versionNo+"'";
            rs = MainMenu.statement.executeQuery(SQL_RER_points);
        }
        catch(SQLException e)
        {
            System.out.println("Activity Type can not be added, Please try again.");
        }
        if(rs.next())
        {
            points = rs.getInt("POINTS");
        }

        try
        {
            String SQL_RER_points = "SELECT TYPE " +
                    "FROM LOYALTY_PROGRAM" +
                    "WHERE BRAND_LP_ID = '"+brandId+"'";
            rs = MainMenu.statement.executeQuery(SQL_RER_points);
        }
        catch(SQLException e)
        {
            System.out.println("Activity Type can not be added, Please try again.");
        }
        if(rs.next())
        {
            type = rs.getString("TYPE");
        }

        String walletId;
        try
        {
            String SQL_RER_points = "SELECT WALLET_ID, POINTS " +
                    "FROM WALLET" +
                    "WHERE BRAND_ID = '"+brandId+"' AND CUST_ID = '"+Login.userId+"'";
            rs = MainMenu.statement.executeQuery(SQL_RER_points);
        }
        catch(SQLException e)
        {
            System.out.println("No points available, Please try again.");
        }
        if(rs.next())
        {
            walletId=rs.getString("WALLET_ID");
            wallet_points = rs.getInt("POINTS");
        }

        // if not a tiered program add into the wallet
        if (type.toLowerCase() == 'regular')
        {
            Total = points + wallet_points;
        }
        // find out if its a tiered program, if so get the tier and the multiplier
        if (type.toLowerCase() == 'tiered')
        {
            //from wallet get the tier status
            try
            {
                String SQL_RER_points = "SELECT TIER_STATUS " +
                        "FROM WALLET" +
                        "WHERE BRAND_ID = '"+brandId+"' AND CUST_ID = '"+Login.userId+"'";
                rs = MainMenu.statement.executeQuery(SQL_RER_points);
            }
            catch(SQLException e)
            {
                System.out.println("Activity Type can not be added, Please try again.");
            }
            if(rs.next())
            {
                tier_status = rs.getString("TIER_STATUS");
            }

            //from tier table, brandid , tier_status, get multiplier
            try
            {
                String SQL_RER_points = "SELECT MULTIPLIER" +
                        "FROM TIER" +
                        "WHERE BRAND_ID = '"+brandId+"' AND TIER_NAME = '"+tier_status+"'";
                rs = MainMenu.statement.executeQuery(SQL_RER_points);
            }
            catch(SQLException e)
            {
                System.out.println("Activity Type can not be added, Please try again.");
            }
            if(rs.next())
            {
                multiplier = rs.getInt("MULTIPLIER");
            }
            Total = wallet_points + multiplier * points;
        }

        try
        {
            PreparedStatement ps = MainMenu.connection.prepareStatement("UPDATE WALLET" +
                    "SET POINTS = " +Total+
                    "WHERE CUST_ID = '"+Login.userId+"' AND BRAND_ID = '"+brandId+"'");
            ps.executeUpdate();
        }
        catch (SQLIntegrityConstraintViolationException e)
        {
            System.out.println("Points cannot be updated, Please try again. " + e);
        }
        catch(SQLException e)
        {
            System.out.println("Points cannot be updated, Please try again. " + e);
        }

        //CODE TO UPDATE WALLET_ACT table and ACTIVITY TABLE
        try {
            ps = MainMenu.connection.prepareStatement("Insert into ACTIVITY (ACT_DATE, ACT_CATEGORY_CODE, VALUE) values (?,?,?)");
            ps.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now()));
            ps.setString(2, acc);
            ps.setString(3, activity_value);
        } catch (SQLException e) {
            System.out.println("SQL Exception encountered");
        } catch (SQLIntegrityConstraintViolation e) {
            System.out.println("Integrity Constraint Violation");
        }

        String activityId;
        try {
            String activityIdSelect = "select MAX(ACT_ID) AS MAX_ACT_ID from ACTIVITY;
            rs4 = MainMenu.statement.executeQuery(activityIdSelect);

            if (rs4.next()) {
                activityId = rs4.getInt("MAX_ACT_ID");
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception encountered");
        }
        //entry into wallet_acitivity_bridgetable
        try {
            ps = MainMenu.connection.prepareStatement("Insert into WALLET_ACTIVITY(WALLET_ID, ACT_ID) values (?,?)");
            ps.setString(1, walletId);
            ps.setString(2, activityId);
        } catch (SQLIntegrityConstraintViolation e) {
            System.out.println("Integrity Constraint Violation");
        }

    }

    public static void redeemPoints()
    {
        ResultSet rs;
        Scanner sc = new Scanner(System.in);
        //PreparedStatement pstmt = null;
        Map<String, String> brands_rp = new HashMap();
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
            System.out.println("Activity Type can not be selected, Please try again.");
        }
        if(brands_rp.isEmpty())
        {
            System.out.println("Please enroll in a brand to redeem points.");
            customerPage();
        }

        boolean value = false;
        String selected_value;
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

        //CODE TO FETCH REWARD ACTIVTIES FOR SELECTED BRAND
        for (Entry<String, String> entry : brands_rp.entrySet()) {
            if (entry.getValue().equals(selected_value)) {
                String brandId = entry.getKey();
                break;
            }
        }

        List<String> brands_rcc = new ArrayList();
        try
        {
            String SQL_Wallet_rp = "SELECT REWARD_CATEGORY_CODE" +
                    "FROM REWARD" +
                    "WHERE S.BRAND_ID = '"+brandId+"' AND QUANTITY > 0";
            rs = MainMenu.statement.executeQuery(SQL_Wallet_rp);
            while(rs.next())
            {
                brands_rcc.add(rs.getString("REWARD_CATEGORY_CODE"));
            }
        }
        catch(SQLException e)
        {
            System.out.println("Activity Type can not be selected, Please try again.");
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

        Map<String, Integer> brands_rrr = new HashMap();
        //from the RRR table figure out which rewards are supported for this brand and display that
        try
        {
            String SQL_Wallet_rp = "SELECT R.REWARD_NAME, MAX(S.VERSION_NO)" +
                    " FROM REWARD_TYPE R, RR_RULES S" +
                    " WHERE S.BRAND_ID = '"+brandId+"' AND R.RT_ID = S.REWARD_CATEGORY_CODE AND R.RT_ID IN " + str +
                    " GROUP BY R.REWARD_NAME"
            rs = MainMenu.statement.executeQuery(SQL_Wallet_rp);
            while(rs.next())
            {
                brands_rrr.put(rs.getString("REWARD_NAME"), rs.getInt("VERSION_NO"));
            }
        }
        catch(SQLException e)
        {
            System.out.println("Activity Type can not be selected, Please try again.");
        }


        boolean value = false;
        String selected_reward, R_C_C;
        int points;
        //TODO: Identifier, Name
        while(!value)
        {
            System.out.println("List of rewards from which you can select:");
            for(String item : brands_rrr.keys())
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
        for (Entry<String, String> entry : brands_rrr.entrySet()) {
            if (entry.getKey().equals(selected_reward)) {
                String version = entry.getValue();
                break;
            }
        }

        try
        {
            String SQL_Reward = "SELECT S.POINTS, R.RT_ID " +
                    "FROM REWARD_TYPE R, RR_RULES S " +
                    "WHERE S.BRAND_ID = '"+brandId+"' AND R.RT_ID = S.REWARD_CATEGORY_CODE AND S.VERSION_NO = '"+version+"'" +
                    " AND R.REWARD_NAME = '"+selected_reward+"'";
            rs = MainMenu.statement.executeQuery(SQL_Reward);
        }
        catch(SQLException e)
        {
            System.out.println("Activity Type can not be selected, Please try again.");
        }
        if(rs.next())
        {
            points = rs.getInt("POINTS");
            R_C_C = rs.getString("RT_ID");
        }

        int walletPts;
        String walletId;
        try
        {
            String SQL_WalletPts = "SELECT W.POINTS, W.WALLET_ID " +
                    "FROM WALLET W " +
                    "WHERE W.BRAND_ID = '"+brandId+"' AND W.CUST_ID = '"+Login.userId+"'";
            rs = MainMenu.statement.executeQuery(SQL_WalletPts);
        }
        catch(SQLException e)
        {
            System.out.println("Activity Type can not be selected, Please try again.");
        }
        if(rs.next()) {
            walletPts = rs.getInt("POINTS");
            walletId = rs.getString("WALLET_ID");
        } else {
            System.out.println("Wallet could not be found");
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
                        " WHERE WALLET_ID = '"+walletId+"' ");
                ps.executeUpdate();
            }
            catch(SQLException e)
            {
                System.out.println("Wallet could not be updated");
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
            }

            String redeemCategoryCode;
            try {
                String sqlActCategorySelect = "select AT_ID from ACTIVITY_TYPE where ACTIVITY_NAME='REDEEM'";
                ResultSet rs3 = MainMenu.statement.executeQuery(sqlActCategorySelect);
                if (rs3.next()) {
                    redeemCategoryCode = rs3.getString("AT_ID");
                }
                //entry into activity table
                ps = MainMenu.connection.prepareStatement("Insert into ACTIVITY (ACT_DATE, ACT_CATEGORY_CODE, VALUE) values (?,?,?)");
                ps.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now()));
                ps.setString(2, redeemCategoryCode);
                ps.setString(3, R_C_C);
            } catch (SQLException e) {
                System.out.println("SQL Exception encountered");
            } catch (SQLIntegrityConstraintViolation e) {
                System.out.println("Integrity Constraint Violation");
            }

            String activityId;
            try {
                String activityIdSelect = "select MAX(ACT_ID) AS MAX_ACT_ID from ACTIVITY;
                rs4 = MainMenu.statement.executeQuery(activityIdSelect);

                if (rs4.next()) {
                    activityId = rs4.getInt("MAX_ACT_ID");
                }
            } catch (SQLException e) {
                System.out.println("SQL Exception encountered");
            }
            //entry into wallet_acitivity_bridgetable
            try {
                ps = MainMenu.connection.prepareStatement("Insert into WALLET_ACTIVITY(WALLET_ID, ACT_ID) values (?,?)");
                ps.setString(1, walletId);
                ps.setString(2, activityId);
            } catch (SQLIntegrityConstraintViolation e) {
                System.out.println("Integrity Constraint Violation");
            }

            if(selected_reward.toLowerCase().equals("gift card"))
            {
                try {
                    ps = MainMenu.connection.prepareStatement("Insert into WALLET_GIFTCARD(WALLET_ID) values (?)");
                    ps.setString(1, walletId);
                } catch (SQLIntegrityConstraintViolation e) {
                    System.out.println("Integrity Constraint Violation");
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