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

        String sqlCustName = "SELECT NAME FROM CUSTOMER WHERE CUST_ID IN (SELECT CUST_ID FROM WALLET WHERE CUST_ID NOT IN (SELECT CUST_ID FROM WALLET WHERE BRAND_ID='Brand02'))";

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
            showQueryPage();
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
            if (rs.next()) {
                while (rs.next()) {
                    System.out.println(rs.getString("REWARD_NAME"));
                }
            } else {
                System.out.println("No Reward Found.");
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
            if (rs.next()) {
                while (rs.next()) {
                    System.out.println(rs.getString("NAME"));
                }
            } else {
                System.out.println("No Loyalty Program Found.");
            }
            rs.close();
            showQueryPage();
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
            showQueryPage();
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

            String sql = "Select  BRAND_ID, SUM(POINTS) AS SUMPOINTS from" +
                    "(select ACT_ID, VALUE from ACTIVITY A where A.ACT_CATEGORY_CODE ='"+ACC+"' " +
                    "NATURAL INNER JOIN" +
                    "select WALLET_ID, ACT_ID, BRAND_ID from WALLET_ACTIVITY W, WALLET W1" +
                    "where W.WALLET_ID = W1.WALLET_ID" +
                    "on A.ACT_ID = W.ACT_ID ) as temp, RR_RULES R" +
                    "where temp.BRAND_ID = R.BRAND_ID and temp.VALUE = R.REWARD_CATEGORY_CODE " +
                    "GROUPBY BRAND_ID" +
                    "HAVING SUM(POINTS)<500";
            rs = MainMenu.statement.executeQuery(sql);

            List<String> brandIds=new ArrayList<String>();
            while(rs.next()) {
                if(rs.getInt("SUMPOINTS") < 500) {
                    brandIds.add(rs.getString("BRAND_ID"));
                }
            }

            if(brandIds.size() == 0) {
                System.out.println("No redeem activity has been performed for any brand!");
                showQueryPage();
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("(");
                for (String i : brandIds){
                    sb.append(i+",");
                }
                sb.deleteCharAt(sb.length() -1);
                sb.append(")");
                String str = sb.toString();
                String sqlQuery = "SELECT NAME FROM BRAND WHERE BRAND_ID IN " + str;
                rs = MainMenu.statement.executeQuery(sqlQuery);
                str="";
                System.out.println("Brands: ");
                while(rs.next()) {
                    str=str+" "+rs.getString("NAME");
                }
                System.out.print(str);
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
            if (rs.next()) {
                while (rs.next()) {
                    System.out.println(rs.getString("numAct"));
                }
            } else {
                System.out.println("No Activity Found.");
            }
            rs.close();

            showQueryPage();

        } catch (SQLException e) {
            Helper.close(rs);
            e.printStackTrace();
        }
    }
}