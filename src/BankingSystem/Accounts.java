package BankingSystem;

import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class Accounts {
    Connection connection;
    Scanner scanner;

    public Accounts(Connection connection,Scanner scanner) {
        this.connection = connection;
        this.scanner=scanner;
    }
    void open_account(){
        String queryToInsert="insert into accounts(account_number,full_name,email,balance,security_pin) values (?,?,?,?,?) ";
        System.out.print("Enter Your Name : ");
        String name=scanner.nextLine();
        System.out.print("Enter Email : ");
        String email=scanner.nextLine();
        boolean checkEmailExistance=account_exists(email);
        if (checkEmailExistance){
            System.out.println("Email is already exists ");
            return;
        }
        System.out.print("Enter Initial Balance : ");
        int balance=scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Security Pin : ");
        int securityPin=scanner.nextInt();
        scanner.nextLine();
        try {
            PreparedStatement preparedStatement= connection.prepareStatement(queryToInsert);
            long accountNumber=generate_account_number();
            preparedStatement.setLong(1,accountNumber);
            preparedStatement.setString(2,name);
            preparedStatement.setString(3,email);
            preparedStatement.setInt(4,balance);
            preparedStatement.setInt(5,securityPin);
            int rowsAffected=preparedStatement.executeUpdate();
            if (rowsAffected>0){
                System.out.println("Your Account is created ");
                System.out.printf("Your Account Number is : %s",accountNumber);
                System.out.println();
                Thread.sleep(1000);
            }else {
                System.out.println("Account creation failed");
            }
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

    }
    void get_account_number(){
        System.out.print("Please enter registered email id : ");
        String email=scanner.nextLine();
        String query="Select account_number from accounts where email=?";
        try (PreparedStatement preparedStatement= connection.prepareStatement(query);
        ){
            preparedStatement.setString(1,email);
            ResultSet resultSet= preparedStatement.executeQuery();
            if (resultSet.next()){
                long account_number=resultSet.getLong("account_number");
                System.out.printf("Your account number is : %s",account_number);
                resultSet.close();
            }else{
                System.out.println("There is no account with this email");
                resultSet.close();
            }



        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    public long generate_account_number(){
        Random random=new Random();
        long account_number;
        while (true){
            account_number=100000+random.nextInt(900000);
            try {
                String queryToSelect="Select full_name from accounts where account_number=?";
                PreparedStatement preparedStatement= connection.prepareStatement(queryToSelect);
                preparedStatement.setLong(1,account_number);
                ResultSet resultSet= preparedStatement.executeQuery();
                if (!resultSet.next()){
                   resultSet.close();
                   preparedStatement.close();
                    break;
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return  account_number;
    }
    boolean account_exists(String email){
        String queryToCheck="Select email from accounts where email=?";
        try {
            PreparedStatement preparedStatement= connection.prepareStatement(queryToCheck);
            preparedStatement.setString(1,email);
            ResultSet resultSet= preparedStatement.executeQuery();
            if (resultSet.next()){
                resultSet.close();
                preparedStatement.close();
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return  false;
    }
}
