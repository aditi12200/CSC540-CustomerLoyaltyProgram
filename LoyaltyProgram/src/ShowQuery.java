import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;
import java.util.*;
import java.sql.*;


public class ShowQuery {
    public static void showQueryPage() {
        Scanner sc = new Scanner(System.in);
        int enteredValue;
        boolean selected = false;

        do {
            System.out.println("Choose one of the following options");
            System.out.println("1. List all customers that are not part of Brand02's program.");
            System.out.println("2. List customers that have joined a loyalty program but have not participated in any activity in that program.");
            System.out.println("3. List the rewards that are part of Brand01 loyalty program.");
            System.out.println("4. List all the loyalty programs that include \"refer a friend\" as an activity in at least one of their reward rules.");
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
                        break;
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

        String sqlCustName = "SELECT NAME FROM CUSTOMER WHERE CUST_ID NOT IN (SELECT CUST_ID FROM WALLET WHERE BRAND_ID='Brand02')";

        ResultSet rs = null;
        try {
            rs = MainMenu.statement.executeQuery(sqlCustName);
                while(rs.next()){
                    String custName = rs.getString("NAME");
                    System.out.println(custName);
                }
            rs.close();
            showQueryPage();
        } catch (SQLException e) {
            Helper.close(rs);
            e.printStackTrace();
        }
    }

    private static void query2() {
        String sqlCred = "SELECT C.CUST_ID, C.NAME FROM WALLET W,CUSTOMER C WHERE W.CUST_ID = C.CUST_ID AND W.WALLET_ID NOT IN " +
                "(SELECT WA.WALLET_ID FROM WALLET_ACTIVITY WA WHERE WA.ACT_ID IN " +
                "(SELECT A.ACT_ID FROM ACTIVITY A, ACTIVITY_TYPE A1 WHERE A.ACT_CATEGORY_CODE=A1.AT_ID AND A1.ACTIVITY_NAME<>'JOIN')" +
                " GROUP BY WA.WALLET_ID)";

        ResultSet rs = null;
        try {
            rs = MainMenu.statement.executeQuery(sqlCred);

                while (rs.next()) {
                    String custName=rs.getString("NAME");
                    System.out.println(custName);
                }
            rs.close();
            showQueryPage();
        } catch (SQLException e) {
            Helper.close(rs);
            e.printStackTrace();
        }
    }

    private static void query3() {

        String sqlGetReward = "SELECT REWARD_NAME FROM REWARD_TYPE WHERE RT_ID IN (SELECT REWARD_CATEGORY_CODE FROM REWARD WHERE BRAND_ID='Brand01')";

        ResultSet rs = null;
        try {
            rs = MainMenu.statement.executeQuery(sqlGetReward);
                while (rs.next()) {
                    System.out.println(rs.getString("REWARD_NAME"));
                }
            rs.close();
            showQueryPage();
        } catch (SQLException e) {
            Helper.close(rs);
            e.printStackTrace();
        }
    }

    private static void query4(){
        String sqlGetLpName = "SELECT BRAND_ID FROM RE_RULES WHERE ACT_CATEGORY_CODE=(SELECT AT_ID FROM ACTIVITY_TYPE WHERE ACTIVITY_NAME='Refer A Friend')";

        ResultSet rs = null;
        try {
            rs = MainMenu.statement.executeQuery(sqlGetLpName);
                while (rs.next()) {
                    System.out.println(rs.getString("BRAND_ID"));
                }
            rs.close();
            showQueryPage();
        } catch (SQLException e) {
            Helper.close(rs);
            e.printStackTrace();
        }
    }

    private static void query5() {
        String sqlCred = "SELECT TEMP1.ACTIVITY_NAME, COUNT(*) AS ACT_COUNT FROM " +
                "(SELECT A.ACT_ID, A1.ACTIVITY_NAME FROM ACTIVITY A, ACTIVITY_TYPE A1 WHERE A.ACT_CATEGORY_CODE=A1.AT_ID AND A1.VISIBLE='Y') TEMP1, " +
                "(SELECT W.WALLET_ID, W.ACT_ID FROM WALLET_ACTIVITY W WHERE W.WALLET_ID IN (SELECT WALLET_ID FROM WALLET WHERE BRAND_ID='Brand01')) TEMP2 " +
                "WHERE TEMP1.ACT_ID=TEMP2.ACT_ID GROUP BY TEMP1.ACTIVITY_NAME";

        ResultSet rs = null;
        try {
            rs = MainMenu.statement.executeQuery(sqlCred);
                while (rs.next()) {
                    System.out.println(rs.getString("ACTIVITY_NAME"));
                    System.out.println(rs.getString("ACT_COUNT"));
                }
            rs.close();
            showQueryPage();
        } catch (SQLException e) {
            Helper.close(rs);
            e.printStackTrace();
        }
    }

    private static void query6() {
        String sqlCred = "SELECT CUST_ID FROM WALLET W, WALLET_ACTIVITY WA, ACTIVITY A WHERE W.WALLET_ID = WA.WALLET_ID AND" +
                " WA.ACT_ID = A.ACT_ID AND W.BRAND_ID = 'Brand01' AND A.ACT_CATEGORY_CODE = (SELECT AT_ID FROM ACTIVITY_TYPE WHERE " +
                "ACTIVITY_NAME ='REDEEM') GROUP BY CUST_ID HAVING COUNT(*)>1";

        ResultSet rs = null;
        try {
            rs = MainMenu.statement.executeQuery(sqlCred);
                while (rs.next()) {
                    System.out.println(rs.getString("CUST_ID"));
                }
            rs.close();
            showQueryPage();
        } catch (SQLException e) {
            Helper.close(rs);
            e.printStackTrace();
        }
    }

    private static void query7() {
        String getActCode = "SELECT AT_ID from ACTIVITY_TYPE where ACTIVITY_NAME='REDEEM'";


        ResultSet rs = null;
        try {
            rs = MainMenu.statement.executeQuery(getActCode);
            String ACC;

            if (rs.next()) {
                ACC = rs.getString("AT_ID");

                String sql="SELECT B.NAME, FTEMP.SUMPOINTS FROM " +
                           "(SELECT TEMP.BRAND_ID, SUM(R.POINTS) AS SUMPOINTS FROM " +
                           "(SELECT TEMP2. WALLET_ID, TEMP2. BRAND_ID, TEMP1.VALUE FROM " +
                           "(SELECT A.ACT_ID, A.VALUE FROM ACTIVITY A WHERE A.ACT_CATEGORY_CODE='"+ACC+"') TEMP1 " +
                           "INNER JOIN (SELECT W.WALLET_ID, W.ACT_ID, W1.BRAND_ID FROM WALLET_ACTIVITY W, WALLET W1 WHERE W.WALLET_ID=W1.WALLET_ID) TEMP2 "+
                           "ON TEMP1.ACT_ID=TEMP2.ACT_ID) TEMP, RR_RULES R " +
                           "WHERE TEMP.BRAND_ID=R.BRAND_ID AND TEMP.VALUE=R.REWARD_CATEGORY_CODE GROUP BY TEMP.BRAND_ID HAVING SUM(POINTS)<500) FTEMP, BRAND B " +
                           "WHERE FTEMP.BRAND_ID=B.BRAND_ID";
                rs = MainMenu.statement.executeQuery(sql);

                System.out.println("BRAND          SUMPOINTS");
                while(rs.next()) {
                    System.out.println(rs.getString("NAME")+"        "+rs.getInt("SUMPOINTS"));
                }

            } else {
                System.out.println("No Brand Found.");

            }

            rs.close();
            showQueryPage();
        } catch (SQLException e) {
            Helper.close(rs);
            e.printStackTrace();
        }
    }

    private static void query8() {


        ResultSet rs = null;
        String sqlCred = "select count(*) as numAct from ACTIVITY where ACT_ID in " +
                "(select ACT_ID from WALLET_ACTIVITY where WALLET_ID = " +
                "(select WALLET_ID from WALLET where CUST_ID ='C0003' and BRAND_ID ='Brand02' ) " +
                "and ACT_DATE between '01-AUG-21' and '30-SEP-21')";

        try {

            rs = MainMenu.statement.executeQuery(sqlCred);
                while (rs.next()) {
                    System.out.println(rs.getString("numAct"));
                }
            rs.close();

            showQueryPage();

        } catch (SQLException e) {
            Helper.close(rs);
            e.printStackTrace();
        }
    }
}