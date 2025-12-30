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
import java.sql.ResultSet;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/plain");
        response.getWriter().write("Login servlet is running");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        setCorsHeaders(response);

        try {
            BufferedReader reader = request.getReader();
            Gson gson = new Gson();
            LoginRequest login = gson.fromJson(reader, LoginRequest.class);

            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://mysql-omkar-user-auth.alwaysdata.net:3306/omkar-user-auth_db" +
                            "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

            String dbUser = "448170";
            String dbPass = "Omkar@123";

            Connection conn = DriverManager.getConnection(url, dbUser, dbPass);

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT password FROM users WHERE username = ?"
            );
            ps.setString(1, login.username);

            ResultSet rs = ps.executeQuery();

            if (rs.next() && rs.getString("password").equals(login.password)) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Login successful");
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid username or password");
            }

            rs.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Server error");
        }
    }

    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        setCorsHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void setCorsHeaders(HttpServletResponse res) {
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    static class LoginRequest {
        String username;
        String password;
    }
}
