package BankingSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class User {

    Connection connection;
    Scanner scanner ;
    Accounts accounts;

    public User(Connection connection,Scanner scanner,Accounts accounts) {
        this.connection = connection;
        this.scanner = scanner;
        this.accounts=accounts;
    }

    void register() {
        String queryToInsert = "Insert into user (full_name,email,password) values (?,?,?)";
        while (true) {
            System.out.print("Enter full Name : ");
            String name = scanner.nextLine();

            System.out.print("Enter email id : ");
            String email = scanner.nextLine();

            System.out.print("Enter the password : ");
            String password = scanner.nextLine();

            System.out.println("1 = Register / 2 = Edit : ");
            int option = scanner.nextInt();
            scanner.nextLine();

            if (option==2){
                continue;
            }
                boolean checkExistance=user_exists(email);
                if (checkExistance){
                    System.out.println("This email is already existed...");
                    continue;
                }
                    try (PreparedStatement preparedStatement= connection.prepareStatement(queryToInsert)){
                        preparedStatement.setString(1,name);
                        preparedStatement.setString(2,email);
                        preparedStatement.setString(3,password);
                        int rows = preparedStatement.executeUpdate();

                        if(rows > 0){
                            System.out.println("Registration successful.");
                        }
                        break;

                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }
    }
    String login(){
        String queryToCheck="SELECT * FROM user WHERE email = ? AND password = ?";

        while (true) {

            System.out.print("Enter email : ");
            String email = scanner.nextLine();

            System.out.print("Enter Password : ");
            String password = scanner.nextLine();

            try (PreparedStatement preparedStatement = connection.prepareStatement(queryToCheck)) {

                preparedStatement.setString(1,email);
                preparedStatement.setString(2,password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {

                    if (resultSet.next()){
                        System.out.println("Login Successful.");
                        return email;
                    } else {
                        System.out.println("Invalid Email or Password. Try Again.");
                    }

                }

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    boolean user_exists(String email){
        String query = "SELECT 1 FROM user WHERE email=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }
}