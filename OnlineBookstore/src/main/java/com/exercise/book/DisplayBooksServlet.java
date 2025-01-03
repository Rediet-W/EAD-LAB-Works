package com.exercise.book;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class DisplayBooksServlet extends HttpServlet {
    private DBConnectionManager dbManager = new DBConnectionManager();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String query = "SELECT * FROM books";

        try (Connection connection = dbManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            out.println("<html><body>");
            out.println("<h1>Books List</h1>");
            out.println("<table border='1'>");
            out.println("<tr><th>ID</th><th>title</th><th>Author</th><th>Price</th></tr>");

            while (resultSet.next()) {
                out.println("<tr>");
                out.println("<td>" + resultSet.getInt("id") + "</td>");
                out.println("<td>" + resultSet.getString("title") + "</td>");
                out.println("<td>" + resultSet.getString("author") + "</td>");
                out.println("<td>" + resultSet.getDouble("price") + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("</body></html>");
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p>Error fetching tasks: " + e.getMessage() + "</p>");
        }
    }
}
