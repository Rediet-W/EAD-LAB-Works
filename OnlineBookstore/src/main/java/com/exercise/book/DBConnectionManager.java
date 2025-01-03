package com.exercise.book;

//Rediet Woudma Gamie
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DBConnectionManager {
    private static final String URL = "jdbc:mysql://localhost:3306/bookstoredb"; 
    private static final String USER = "root"; 
    private static final String PASSWORD = "Rediet@1316"; 

    public Connection getConnection() throws SQLException {
    	try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load MySQL JDBC Driver.");
            e.printStackTrace(); 
        }

        System.out.println("Attempting database connection...");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        DBConnectionManager dbManager = new DBConnectionManager();
        try (Connection connection = dbManager.getConnection()) {
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
