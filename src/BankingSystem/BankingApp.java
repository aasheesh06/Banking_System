package BankingSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class BankingApp {
    private static final String url="jdbc:mysql://127.0.0.1:3306/Banking_System";
    private  static final String userId="your_my_sql_username";
    private static final String password="your_mysql_password";
    public static void main(String[] args) {
        try ( Scanner scanner=new Scanner(System.in);
              Connection connection= DriverManager.getConnection(url,userId,password);){

            Accounts accounts=new Accounts(connection,scanner);
            User user=new User(connection,scanner,accounts);
            AccountManager accountManager=new AccountManager(connection,scanner);

            System.out.println("Welcome to Aash Banking");
            while (true){

            System.out.println("1. Register");
            System.out.println("2. Login");
            int num=scanner.nextInt();
            scanner.nextLine();

             switch (num) {
                 case 1:
                     user.register();
                     break;
                 case 2:

                     String email=user.login();

                     if (email!=null){
                         while (true){
                     System.out.println("1. Open Bank Account");
                     System.out.println("2. Deposit");
                     System.out.println("3. Check Balance");
                     System.out.println("4. Transfer");
                     System.out.println("5. Get Account number.");
                     System.out.println("6. Debit by ATM  ");
                     System.out.println("7. Exit");
                     int choosedNum = scanner.nextInt();
                     scanner.nextLine();
                     switch (choosedNum) {
                         case 1:
                             accounts.open_account();
                             break;
                         case 2:
                             accountManager.credit_money(email);
                             break;
                         case 3:
                             accountManager.check_balance(email);
                             break;
                         case 4:
                             accountManager.transfer_money(email);
                             break;
                         case 5:
                             accounts.get_account_number();
                             break;
                         case 6:
                             accountManager.debit_money(email);
                             break;
                         case 7:
                             exit();
                             break;
                         default:
                             System.out.println("Invalid Choice");


                     }
                     }
                     }
                     break;
                 case 3:
                     exit();
                     return;
                 default:
                     System.out.println("Invalid Choice");
             }
            }
        } catch (SQLException e) {

            System.out.println(e.getMessage());
        }
    }
    public static void exit (){
        System.out.println("Exiting System ");
        for (int i = 5; i > 1; i--) {
            System.out.print(".");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
        System.out.println("Thank you for visiting Aash Bankingh App");
    }
}
