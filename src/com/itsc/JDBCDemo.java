package com.itsc;
import java.sql.Connection;

import java.sql.DriverManager;

public class JDBCDemo {
    public static void main(String[] args) {
        try {
            
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/student_management";
            String username = "root"; 
            String password = "Rediet@1316";

            
            Class.forName(driver); 
            
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Established Connection to StudentsDB");

            
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
