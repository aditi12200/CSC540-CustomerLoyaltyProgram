import java.util.Scanner;
import
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
                        //TODO
                        break;
                    case 3:
                        //TODO
                        break;
                    case 4:
                        //TODO
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

        try{
            String sqlLoyaltyProgramSelect = "select * from LOYALTY_PROGRAM L, BRAND B where L.BRAND_ID=B.BRAND_ID and L.STATE='active'";
            ResultSet rs = MainMenu.statement.executeQuery(sqlLoyaltyProgramSelect);
            while (rs.next()){
                String loyaltyProgram = rs.getString("NAME");
                availableLoyaltyPrograms.add(loyaltyProgram);
            }
        } catch(SQLException e){
            System.out.println("No active loyalty programs at the moment.")
        }

        boolean correctValue=false;
        while (!correctValue){
            System.out.println("List of available loyalty programs: ");

            for(String prog: availableLoyaltyPrograms){
                System.out.println(prog);
            }

            System.out.println("Enter the loyalty program you want to enroll in: ");
            chosenLoyaltyProgram=sc.nextLine();

            correctValue=availableLoyaltyPrograms.contains(chosenLoyaltyProgram);
            if(!correctValue) {
                System.out.println("Chosen loyalty program doesn't exist. Choose again.")
            }
        }
        //TODO : enter the chosen loyalty program in respective tables.
        // -> also implement go back
    }

    public static void viewWallet(){
        String custId=Login.userId;
        Scanner sc=new Scanner(System.in);
        int enteredValue;
        do{
            enteredValue = Helper.selectNextOption(sc, "viewWallet");

            if(enteredValue==2){
                customerPage();
            } else{
                try{
                    String walletSelect="select * from WALLET where WALLET.CUST_ID="+custId;
                    ResultSet rs=MainMenu.statement.executeQuery(walletSelect);
                    System.out.println("Wallet ID\tBrand ID\tCustomer ID\tPoints\tTier Status");
                    while(rs.next()){
                        String walletId = rs.getString("WALLET_ID");
                        String brandId = rs.getString("BRAND_ID");
                        String custId=rs.getString("CUST_ID");
                        int points=rs.getInt("POINTS");
                        String tierStatus=rs.getString("TIER_STATUS");

                        System.out.println(walletId+"\t"+brandId+"\t"+custId+"\t"+points+"\t"+tierStatus);
                    }
                } catch(SQLException e){
                    System.out.println("You do not have any existing wallets.");
                }
            }
        } while(enteredValue!=2)
    }
}