
package com.exercise.book;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class BookRegistrationServlet extends HttpServlet {
	private DBConnectionManager dbManager = new DBConnectionManager();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String title = request.getParameter("title");
        String author = request.getParameter("author"); 
        String price = request.getParameter("price"); 

        String query = "INSERT INTO books (title, author, price) VALUES (?, ?, ?)";

        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setString(3, price);

            int rowsInserted = statement.executeUpdate();

            PrintWriter out = response.getWriter();
            out.println(rowsInserted > 0 ? "Book registered successfully!" : "Failed to register a book.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
