import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;


public class ShowQuery {
    public static void showQueryPage() {
        Scanner sc = new Scanner(System.in);
        int enteredValue;
        boolean selected = false;

        do {
            System.out.println("Choose one of the following options");
            System.out.println("1. List all customers that are not part of Brand02’s program.");
            System.out.println("2. List customers that have joined a loyalty program but have not participated in any activity in that program.");
            System.out.println("3. List the rewards that are part of Brand01 loyalty program.");
            System.out.println("4. List all the loyalty programs that include “refer a friend” as an activity in at least one of their reward rules.");
            System.out.println("5. For Brand01, list for each activity type in their loyalty program, the number instances that have occurred.");
            System.out.println("6. List customers of Brand01 that have redeemed at least twice.");
            System.out.println("7. All brands where total number of points redeemed overall is less than 500 points");
            System.out.println("8. For Customer C0003, and Brand02, number of activities they have done in the period of 08/1/2021 and 9/30/2021.");
            System.out.println("9. Go Back");

            try {
                enteredValue = sc.nextInt();
                selected = true;

                switch (enteredValue) {
                    case 1:
                        query1();
                        break;
                    case 2:
                        query2();
                        break;
                    case 3:
                        query3();
                        break;
                    case 4:
                        query4();
                        break;
                    case 5:
                        query5();
                        break;
                    case 6:
                        query6();
                    case 7:
                        query7();
                        break;
                    case 8:
                        query8();
                        break;
                    case 9:
                        MainMenu.displayMenu();
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

    private static void query1() {

        String sqlCustName = "select NAME  from CUSTOMER where CUST_ID in (select CUST_ID from wallet " +
                "where CUST_ID not in (select CUST_ID from wallet where BRAND_ID = " +
                "(Select BRAND_ID from BRAND where NAME ='Brand02')))"

        ResultSet rs = null;
        try {
            rs = MainMenu.statement.executeQuery(sqlCustName);
            if (rs.next()) {
                while(rs.next()){
                    String custName = rs.getString("NAME");
                    System.out.println(custName);
                }
            } else {
                System.out.println("No Customer Found.");
            }

            rs.close();
        } catch (SQLException e) {
            Helper.close(rs);
            e.printStackTrace();
        }
    }

    private static void query2() {
        String sqlCred = "SELECT C.CUST_ID, C.NAME FROM WALLET W,CUSTOMER C WHERE W.WALLET_ID NOT IN " +
                "(SELECT WA.WALLET_ID FROM WALLET_ACTIVITY WA WHERE WA.ACT_ID IN " +
                "(SELECT A.ACT_ID FROM ACTIVITY A, ACTIVITY_TYPE A1 WHERE A.ACT_CATEGORY_CODE=A1.AT_ID AND A1.ACTIVITY_NAME<>'JOIN')" +
                "GROUP BY WA.WALLET_ID)";

        ResultSet rs = null;
        try {
            rs = MainMenu.statement.executeQuery(sqlCred);
            if (rs.next()) {
                while (rs.next()) {
                    String custName=rs.getString("NAME");
                    System.out.println(custName);
                }
            } else {
                System.out.println("No Customer Found.");
            }

            rs.close();
        } catch (SQLException e) {
            Helper.close(rs);
            e.printStackTrace();
        }
    }

    private static void query3() {
        String sqlGetReward = "select REWARD_NAME from REWARD_TYPE where RT_ID IN" +
                "(select REWARD_CATEGORY_CODE from REWARD where BRAND_ID = " +
                "(select BRAND_ID from BRAND where NAME = 'Brand01'))";

        ResultSet rs = null;
        try {
            rs = MainMenu.statement.executeQuery(sqlGetReward);
            if (rs.next()) {
                while (rs.next()) {
                    System.out.println(rs.getString("REWARD_NAME"));
                }
            } else {
                System.out.println("No Reward Found.");
            }

            rs.close();
        } catch (SQLException e) {
            Helper.close(rs);
            e.printStackTrace();
        }
    }

    private static void query4(){
        String sqlGetLpName = "select NAME from BRAND where BRAND_ID in " +
                "(select BRAND_ID from RE_RULES where ACT_CATEGORY_CODE = " +
                "(select AT_ID from ACTIVITY_TYPE where NAME = 'Refer A Friend')) ";

        ResultSet rs = null;
        try {
            rs = MainMenu.statement.executeQuery(sqlGetLpName);
            if (rs.next()) {
                while (rs.next()) {
                    System.out.println(rs.getString("NAME"));
                }
            } else {
                System.out.println("No Loyalty Program Found.");
            }

            rs.close();
        } catch (SQLException e) {
            Helper.close(rs);
            e.printStackTrace();
        }
    }

    private static void query5() {
        String sqlCred = "SELECT TEMP.ACTIVITY_NAME, COUNT(*) AS ACT_COUNT FROM" +
                "((SELECT A.ACT_ID, A1.ACTIVITY_NAME FROM ACTIVITY A, ACTIVITY_TYPE A1 WHERE A.ACT_CATEGORY_CODE" +
                "=A1.AT_ID AND A1.VISIBLE='Y')" +
                "NATURAL INNER JOIN" +
                "(SELECT * FROM WALLET_ID, ACT_ID FROM WALLET_ACTIVITY WHERE WALLET_ID IN " +
                "(SELECT W.WALLET_ID FROM WALLET W,BRAND B WHERE W.BRAND_ID=B.BRAND_ID AND B.NAME='Brand01'))) AS TEMP" +
                "GROUP BY TEMP.ACTIVITY_NAME";

        ResultSet rs = null;
        try {
            rs = MainMenu.statement.executeQuery(sqlCred);
            if (rs.next()) {
                while (rs.next()) {
                    System.out.println(rs.getString("ACTIVITY_NAME"));
                    System.out.println(rs.getString("ACT_COUNT"));
                }
            } else {
                System.out.println("No Activity Type Found.");
            }

            rs.close();
        } catch (SQLException e) {
            Helper.close(rs);
            e.printStackTrace();
        }
    }

    private static void query6() {
        String sqlCred = "SELECT NAME, COUNT(*) FROM CUSTOMER WHERE CUST_ID IN " +
                "(SELECT TEMP.CUST_ID FROM" +
                "((SELECT A.ACT_ID FROM ACTIVITY A, ACTIVITY_TYPE A1 WHERE A.ACT_CATEGORY_CODE=A1.AT_ID" +
                " AND A1.ACTIVITY_NAME='REDEEM')" +
                "NATURAL INNER JOIN" +
                "(SELECT W.ACT_ID, W1.CUST_ID FROM WALLET_ACTIVITY W, WALLET W1,BRAND B WHERE W.WALLET_ID=W1.WALLET_ID AND" +
                "B.BRAND_ID=W1.BRAND_ID AND B.NAME='Brand01')) AS TEMP)" +
                "GROUP BY NAME" +
                "HAVING COUNT(*)>1";

        ResultSet rs = null;
        try {
            rs = MainMenu.statement.executeQuery(sqlCred);
            if (rs.next()) {
                while (rs.next()) {
                    System.out.println(rs.getString("NAME"));
                }
            } else {
                System.out.println("No Customer Found.");
            }

            rs.close();
        } catch (SQLException e) {
            Helper.close(rs);
            e.printStackTrace();
        }
    }

    private static void query7() {
        String sqlCred = "SELECT * FROM TABLE(show_query_7)";

        ResultSet rs = null;
        try {
            rs = MainMenu.statement.executeQuery(sqlCred);
            if (rs.next()) {
                while (rs.next()) {
                    //TODO
                }
            } else {
                System.out.println("No Brand Found.");
            }

            rs.close();
        } catch (SQLException e) {
            Helper.close(rs);
            e.printStackTrace();
        }
    }

    private static void query8() {

        String getCustID = "select CUST_ID from CUSTOMER where NAME ='C003'";
        String getBrandID = "select BRAND_ID from BRAND where NAME ='Brand02'";

        ResultSet rs = null;
        try {
            rs = MainMenu.statement.executeQuery(getCustID);
            String custID, brandID;
            if(rs.next()) {
                 custID = rs.getString("CUST_ID");
            } else {
                System.Out.println("Customer not found");
            }
            rs = MainMenu.statement.executeQuery(getBrandID);
            if(rs.next()){
                brandID = rs.getString("BRAND_ID");
            } else {
                System.Out.println("Brand not found");
            }
            String sqlCred = "select count(*) as numAct from ACTIVITY where ACT_ID in " +
                    "(select ACT_ID from WALLET_ACTIVITY where WALLET_ID = " +
                    "(select WALLET_ID from WALLET where CUST_ID ='"+custID+"' and BRAND_ID ='"+brandID+"' ) " +
                    "and ACT_DATE between '01-AUG-21' and '30-SEP-21')";
            rs = MainMenu.statement.executeQuery(sqlCred);
            if (rs.next()) {
                while (rs.next()) {
                    System.Out.println(rs.getString("numAct"));
                }
            } else {
                System.out.println("No Activity Found.");
            }

            rs.close();
        } catch (SQLException e) {
            Helper.close(rs);
            e.printStackTrace();
        }
    }
}