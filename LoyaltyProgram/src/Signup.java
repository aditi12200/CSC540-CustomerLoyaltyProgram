import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;
import java.util.*;
import java.sql.*;

public class Signup {
    public static void signUpPage() {
        Scanner sc = new Scanner(System.in);
        int enteredValue;
        boolean selected = false;

        do {
            System.out.println("Choose one of the following options");
            System.out.println("1. Brand Sign-up");
            System.out.println("2. Customer Sign-up");
            System.out.println("3. Go Back");

            try {
                enteredValue = sc.nextInt();
                selected = true;

                switch (enteredValue) {
                    case 1:
                        addBrand();
                        break;
                    case 2:
                        addCustomer();
                        break;
                    case 3:
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

    public static void addBrand() {
        String brandId, brandPassword, brandName, brandAddress;

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter brand id (This will be used as your loyalty program's id):");
        brandId = sc.nextLine();
        char[] pwd = System.console().readPassword("Enter password:");
        brandPassword = new String(pwd);
        System.out.print("Enter brand name:");
        brandName = sc.nextLine();
        System.out.print("Enter address:");
        brandAddress = sc.nextLine();

        int enteredValue = Helper.selectNextOption(sc, "Sign-up");
        CallableStatement statement = null;

        if (enteredValue == 2) {
            signUpPage();
        } else {
            try {
                statement = MainMenu.connection.prepareCall("{call add_brand(?, ?, ?, ?, ?)}");
                statement.setString(1, brandId);
                statement.setString(2, brandPassword);
                statement.setString(3, brandName);
                statement.setString(4, brandAddress);
                statement.registerOutParameter(5, Types.INTEGER);

                statement.execute();
                int ret = statement.getInt(5);
                statement.close();

                if (ret == 0) {
                    System.out.println("Brand id is already taken. Please try again.");
                    addBrand();
                } else {
                    System.out.println("Brand has been added successfully.");
                    Login.loginPage();
                }
            } catch (SQLException e) {
                Helper.close(statement);
                System.out.println("Brand could not be added.");
                addBrand();
            }
        }
    }

    public static void addCustomer() {
        String customerId, customerPassword, customerName, customerAddress, customerPhone;

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter customer id:");
        customerId = sc.nextLine();
        char[] pwd = System.console().readPassword("Enter password:");
        customerPassword = new String(pwd);
        System.out.print("Enter name:");
        customerName = sc.nextLine();
        System.out.print("Enter address:");
        customerAddress = sc.nextLine();
        System.out.print("Enter phone:");
        customerPhone = sc.nextLine();

        int enteredValue = Helper.selectNextOption(sc, "Sign-up");
        CallableStatement statement = null;

        if (enteredValue == 2) {
            signUpPage();
        } else {
            try {
                statement = MainMenu.connection.prepareCall("{call add_customer(?, ?, ?, ?, ?, ?)}");
                statement.setString(1, customerId);
                statement.setString(2, customerPassword);
                statement.setString(3, customerName);
                statement.setString(4, customerAddress);
                statement.setString(5, customerPhone);
                statement.registerOutParameter(6, Types.INTEGER);

                statement.execute();
                int ret = statement.getInt(6);
                statement.close();

                if (ret == 0) {
                    System.out.println("Customer id is already taken. Please try again.");
                    addCustomer();
                } else {
                    System.out.println("Customer has been added successfully.");
                    Login.loginPage();
                }
            } catch (SQLException e) {
                Helper.close(statement);
                System.out.println("Customer could not be added.");
                addCustomer();
            }
        }
    }
}