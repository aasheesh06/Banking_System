package BankingSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {
    Connection connection;
    Scanner scanner;

    public AccountManager(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    void credit_money(String email) {
        if(!accountExists(email)){
            System.out.println("Account not found. Please open an account first.");
            return;
        }

        String query = "UPDATE accounts SET balance = balance + ? WHERE email=? AND security_pin=?";

        System.out.print("Enter amount : ");
        double amount = scanner.nextDouble();

        if (amount <= 0) {
            System.out.println("Invalid amount");
            return;
        }

        int counter = 0;

        while (counter < 3) {

            System.out.print("Enter pin : ");
            int pin = scanner.nextInt();

            try (PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setDouble(1, amount);
                ps.setString(2, email);
                ps.setInt(3, pin);

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Your account balance is credited.");
                    return;
                } else {
                    counter++;
                    System.out.println("Incorrect PIN");
                }

            } catch (SQLException e) {

                e.printStackTrace();
            }
        }

        System.out.println("Too many attempts. Returning to menu.");
    }

    void debit_money(String email) {
        if(!accountExists(email)){
            System.out.println("Account not found. Please open an account first.");
            return;
        }

        String balanceQuery = "SELECT balance FROM accounts WHERE email=? AND security_pin=?";
        String updateQuery = "UPDATE accounts SET balance = balance - ? WHERE email=? AND security_pin=?";

        System.out.print("Enter amount : ");
        double amount = scanner.nextDouble();

        if (amount <= 0) {
            System.out.println("Invalid amount");
            return;
        }

        int counter = 0;

        while (counter < 3) {

            System.out.print("Enter pin : ");
            int pin = scanner.nextInt();

            try (PreparedStatement ps = connection.prepareStatement(balanceQuery)) {

                ps.setString(1, email);
                ps.setInt(2, pin);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {

                    double balance = rs.getDouble("balance");

                    if (balance >= amount) {

                        try (PreparedStatement ps2 = connection.prepareStatement(updateQuery)) {

                            ps2.setDouble(1, amount);
                            ps2.setString(2, email);
                            ps2.setInt(3, pin);

                            ps2.executeUpdate();

                            System.out.println("Withdrawal successful");
                            return;
                        }

                    } else {
                        System.out.println("Insufficient balance");
                        return;
                    }

                } else {
                    counter++;
                    System.out.println("Incorrect PIN");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Too many incorrect attempts.");

    }
    void transfer_money(String email){
        if(!accountExists(email)){
            System.out.println("Account not found. Please open an account first.");
            return;
        }

        try {
            connection.setAutoCommit(false);

        String queryToVerifyAccount="Select 1 from accounts where account_number=?";

        String queryToVerifyPin="Select 1 from accounts where email=? and security_pin=?";

        String queryToDebit="Update accounts set balance = balance- ? where email=?";

        String queryToCredit="Update accounts set balance = balance+ ? where account_number=?";

        String queryToCheckAmount="Select balance from accounts where email=? and security_pin=?";

        System.out.print("Enter Amount : ");
        double amount=scanner.nextDouble();
        scanner.nextLine();
        if (amount <= 0) {
            System.out.println("Invalid amount");
            return;
        }

        int counter=0;
        while (counter<3){
            System.out.print("Enter Account number : ");
            long accountNumber=scanner.nextLong();
            try (PreparedStatement ps1= connection.prepareStatement(queryToVerifyAccount)){
                ps1.setLong(1,accountNumber);
                try (ResultSet rs1= ps1.executeQuery()){
                    if (!rs1.next()){
                        System.out.println("Invalid account number");
                        counter++;
                        continue;
                    }
                }

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            System.out.print("Enter Pin : ");
            int pin=scanner.nextInt();
            scanner.nextLine();
            try (PreparedStatement ps2= connection.prepareStatement(queryToVerifyPin)){
                ps2.setString(1,email);
                ps2.setInt(2,pin);
                try (ResultSet resultSet= ps2.executeQuery()){
                    if (!resultSet.next()){
                        System.out.println("Incorrect Pin");
                        counter++;
                        continue;
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try (PreparedStatement p5= connection.prepareStatement(queryToCheckAmount)){
                p5.setString(1,email);
                p5.setInt(2,pin);
                try (ResultSet resultSet= p5.executeQuery()){
                    if (resultSet.next()){
                        double checkedAmount=resultSet.getDouble("balance");
                        if (checkedAmount < amount) {
                            System.out.println("Insufficient Balance");
                            return;
                        }
                    }
                }

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            try (PreparedStatement ps3= connection.prepareStatement(queryToDebit);
            PreparedStatement ps4= connection.prepareStatement(queryToCredit)){
                ps3.setDouble(1,amount);
                ps3.setString(2,email);

                ps4.setDouble(1,amount);
                ps4.setLong(2,accountNumber);
                ps3.executeUpdate();
                ps4.executeUpdate();
                connection.commit();
                System.out.println("Transaction Successful");
                return;


            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    System.out.println(e.getMessage());
                }
                System.out.println(e.getMessage());
            }

        }
        System.out.println("To many Attempts");
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }finally {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }


    }
    void check_balance(String email){


        String queryToCheckBalance = "SELECT balance FROM accounts WHERE email=? AND security_pin=?";

        if(!accountExists(email)){
            System.out.println("Account not found. Please open an account first.");
            return;
        }

        int counter = 0;

        while(counter < 3){

            System.out.print("Enter pin: ");
            int pin = scanner.nextInt();
            scanner.nextLine();

            try (PreparedStatement psBalance = connection.prepareStatement(queryToCheckBalance)) {

                psBalance.setString(1,email);
                psBalance.setInt(2,pin);

                try (ResultSet resultSet = psBalance.executeQuery()) {

                    if(resultSet.next()){
                        double checkedAmount = resultSet.getDouble("balance");
                        System.out.printf("Account Balance : %.2f\n", checkedAmount);
                        return;
                    }
                    else{
                        System.out.println("Incorrect Pin");
                        counter++;
                    }

                }

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("Too Many Attempts");
    }
    boolean accountExists(String email){

        String query = "SELECT 1 FROM accounts WHERE email=?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1,email);

            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }
}
