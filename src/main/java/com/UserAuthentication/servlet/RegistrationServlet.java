package com.UserAuthentication.servlet;

import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@WebServlet("/RegisterServlet")
public class RegistrationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/plain");
        response.getWriter().write("Registration servlet is running");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        setCorsHeaders(response);

        try {
            BufferedReader reader = request.getReader();
            Gson gson = new Gson();
            User user = gson.fromJson(reader, User.class);

            if (!user.password.equals(user.retypepassword)) {
                response.setStatus(400);
                response.getWriter().write("Passwords do not match");
                return;
            }

            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://mysql-omkar-user-auth.alwaysdata.net:3306/omkar-user-auth_db" +
                            "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            String dbUser = "448170";
            String dbPass = "Omkar@123";

            Connection conn = DriverManager.getConnection(url, dbUser, dbPass);

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (name, email, username, password) VALUES (?, ?, ?, ?)"
            );

            ps.setString(1, user.name);
            ps.setString(2, user.email);
            ps.setString(3, user.username);
            ps.setString(4, user.password);

            ps.executeUpdate();

            response.setStatus(200);
            response.getWriter().write("Registration successful");

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("Server error");
        }
    }

    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        setCorsHeaders(response);
        response.setStatus(200);
    }

    private void setCorsHeaders(HttpServletResponse res) {
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    static class User {
        String name;
        String email;
        String username;
        String password;
        String retypepassword;
    }
}
