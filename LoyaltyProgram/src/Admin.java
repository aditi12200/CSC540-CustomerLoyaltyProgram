import java.sql.*;
import java.sql.Date;
import java.util.*;

public class Admin {
    public static void adminPage() {
        Scanner sc = new Scanner(System.in);
        int enteredValue;
        boolean flag = false;

        do {
            System.out.println("Choose one of the following options");
            System.out.println("1. Add brand");
            System.out.println("2. Add customer");
            System.out.println("3. Show brand's info");
            System.out.println("4. Show customer's info");
            System.out.println("5. Add activity type");
            System.out.println("6. Add reward type");
            System.out.println("7. Logout");

            try {
                enteredValue = sc.nextInt();
                flag = true;

                switch (enteredValue) {
                    case 1:
                        addBrand();
                        break;
                    case 2:
                        addCustomer();
                        break;
                    case 3:
                        showBrandInfo();
                        break;
                    case 4:
                        showCustomerInfo();
                        break;
                    case 5:
                        addActivityType();
                        break;
                    case 6:
                        addRewardType();
                        break;
                    case 7:
                        adminLogout();
                        break;
                    default:
                        System.out.println("You have made an invalid choice. Please pick again.");
                        flag = false;
                }

            } catch (Exception e) {
                System.out.println("You have made an invalid choice. Please pick again.");
                sc.next();
            }
        } while (!flag);
    }

    public static void showCustomerInfo() {
        String customerUserId;
        int enteredValue;
        Scanner sc = new Scanner(System.in);

        do {
            System.out.print("Enter customer userid:");
            customerUserId = sc.nextLine();

            enteredValue = Helper.selectNextOption(sc, "Show Customer Info");

            if (enteredValue == 2) {
                adminPage();
            } else {
                try {
                    String sqlCustomerSelect = "select * from CUSTOMER where CUST_ID  = '" + customerUserId + "'";

                    ResultSet rs = MainMenu.statement.executeQuery(sqlCustomerSelect);

                    if (rs.next()) {
                        String customerName = rs.getString("NAME");
                        String customerAddress = rs.getString("ADDRESS");
                        String customerPhone = rs.getString("PHONENUMBER");

                        System.out.println("Customer UserId: " + customerUserId);
                        System.out.println("Customer Name: " + customerName);
                        System.out.println("Customer Phone: " + customerPhone);
                        System.out.println("Customer Address: " + customerAddress);
                    } else {
                        System.out.println("Invalid customer user id. Please try again.");
                    }
                } catch (SQLException e) {
                    System.out.println("Customer data could not be fetched. Please try again.");
                }
            }
            sc.nextLine();
        } while (enteredValue != 2);
    }

    public static void showBrandInfo() {
        String brandUserId;
        int enteredValue;
        Scanner sc = new Scanner(System.in);

        do {
            System.out.print("Enter brand id:");
            brandUserId = sc.nextLine();

            enteredValue = Helper.selectNextOption(sc, "Show Brand Info");

            if (enteredValue == 2) {
                adminPage();
            } else {
                try {
                    String sqlBrandSelect = "select * from BRAND where BRAND_ID  = '" + brandUserId + "'";

                    ResultSet rs = MainMenu.statement.executeQuery(sqlBrandSelect);

                    if (rs.next()) {
                        String brandName = rs.getString("NAME");
                        String brandAddress = rs.getString("ADDRESS");
                        Date brandJoinDate = rs.getDate("JOIN_DATE");

                        System.out.println("Brand UserId: " + brandUserId);
                        System.out.println("Brand Name: " + brandName);
                        System.out.println("Brand Address: " + brandAddress);
                        System.out.println("Brand Join Date: " + brandJoinDate);
                    } else {
                        System.out.println("Invalid brand id. Please try again.");
                    }
                } catch (SQLException e) {
                    System.out.println("Could not fetch brand data. Please try again.");
                }
                sc.nextLine();
            }
        } while (enteredValue != 2);
    }

    public static void addBrand() {
        String brandUserId, brandName, brandAddress;

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter brand userid:");
        brandUserId = sc.nextLine();
        System.out.print("Enter name:");
        brandName = sc.nextLine();
        System.out.print("Enter address:");
        brandAddress = sc.nextLine();

        int enteredValue = Helper.selectNextOption(sc, "Add Brand");
        CallableStatement statement = null;

        if (enteredValue == 2) {
            adminPage();
        } else {
            try {
                statement = MainMenu.connection.prepareCall("{call admin_add_brand(?, ?, ?, ?)}");
                statement.setString(1, brandUserId);
                statement.setString(2, brandName);
                statement.setString(3, brandAddress);
                statement.registerOutParameter(4, Types.INTEGER);

                statement.execute();
                int ret = statement.getInt(4);

                if (ret == 0) {
                    System.out.println("Brand user id already exists. Please try again.");
                } else {
                    System.out.println("Brand added successfully.");
                    adminPage();
                }

                statement.close();

                adminPage();
            } catch (SQLException e) {
                Helper.close(statement);
                System.out.println("Brand can not be added. Please try again.");
                adminPage();
            }
        }
    }

    public static void addCustomer() {
        String customerUserId, customerName, customerAddress, customerPhone;

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter customer userid:");
        customerUserId = sc.nextLine();
        System.out.print("Enter name:");
        customerName = sc.nextLine();
        System.out.print("Enter address:");
        customerAddress = sc.nextLine();
        System.out.print("Enter phone:");
        customerPhone = sc.nextLine();

        int enteredValue = Helper.selectNextOption(sc, "Add Customer");
        CallableStatement statement = null;

        if (enteredValue == 2) {
            adminPage();
        } else {
            try {
                statement = MainMenu.connection.prepareCall("{call admin_add_customer(?, ?, ?, ?, ?)}");
                statement.setString(1, customerUserId);
                statement.setString(2, customerName);
                statement.setString(3, customerAddress);
                statement.setString(4, customerPhone);
                statement.registerOutParameter(5, Types.INTEGER);

                statement.execute();

                int ret = statement.getInt(5);
                if (ret == 0) {
                    System.out.println("Customer user id already exists. Please try again.");
                } else {
                    System.out.println("Customer added successfully.");
                    adminPage();
                }

                statement.close();

                adminPage();
            } catch (SQLException e) {
                Helper.close(statement);
                System.out.println("Customer can not be added. Please try again.");
                adminPage();
            }
        }
    }

    public static void addActivityType() {
        String activityName, activityCode;
        int enteredValue;

        Scanner sc = new Scanner(System.in);

        do {
            System.out.print("Enter activity code:");
            activityCode = sc.nextLine();
            System.out.print("Enter activity name:");
            activityName = sc.nextLine();

            enteredValue = Helper.selectNextOption(sc, "Add Activity Type");

            if (enteredValue == 2) {
                adminPage();
            } else {
                try {
                    PreparedStatement ps = MainMenu.connection.prepareStatement("Insert into ACTIVITY_TYPE (AT_ID, ACTIVITY_NAME) values (?,?)");
                    ps.setString(1, activityCode);
                    ps.setString(2, activityName);

                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        System.out.println("Activity Type added successfully.");
                    } else {
                        System.out.println("Activity Type can not be added. Please try again.");
                    }
                } catch (SQLIntegrityConstraintViolationException e) {
                    System.out.println("Activity Type already exists, Please try again.");
                } catch(SQLException e)
                {
                    System.out.println("Activity Type can not be added, Please try again.");
                }
            }

            sc.nextLine();
        } while (enteredValue != 2);
    }

    public static void addRewardType() {
        String rewardName, rewardCode;
        int enteredValue;

        Scanner sc = new Scanner(System.in);

        do {
            System.out.print("Enter reward code:");
            rewardCode = sc.nextLine();
            System.out.print("Enter reward name:");
            rewardName = sc.nextLine();

            enteredValue = Helper.selectNextOption(sc, "Add Reward Type");

            if (enteredValue == 2) {
                adminPage();
            } else {
                try {
                    PreparedStatement ps = MainMenu.connection.prepareStatement("Insert into REWARD_TYPE (RT_ID, REWARD_NAME) values (?,?)");
                    ps.setString(1, rewardCode);
                    ps.setString(2, rewardName);

                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        System.out.println("Reward Type added successfully.");
                    } else {
                        System.out.println("Reward Type can not be added, Please try again.");
                    }
                } catch (SQLIntegrityConstraintViolationException e) {
                    System.out.println("Reward Type already exists, Please try again.");
                } catch(SQLException e)
                {
                    System.out.println("Reward Type can not be added, Please try again.");
                }
            }

            sc.nextLine();
        } while (enteredValue != 2);
    }

    public static void adminLogout() {
        MainMenu.displayMenu();
    }
}