package com.exercise.book;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class DeleteBookServlet extends HttpServlet {
    private DBConnectionManager dbManager = new DBConnectionManager();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");

        String taskId = request.getParameter("id");

        if (taskId == null || taskId.isEmpty()) {
            response.getWriter().println("Invalid task ID");
            return;
        }

        String query = "DELETE FROM books WHERE id = ?";

        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, Integer.parseInt(taskId));
            int rowsDeleted = statement.executeUpdate();

            response.getWriter().println(rowsDeleted > 0 ? "Book deleted successfully!" : "Book not found.");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error deleting book: " + e.getMessage());
        }
    }
}
