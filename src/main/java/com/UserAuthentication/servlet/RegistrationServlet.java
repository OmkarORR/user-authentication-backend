package com.UserAuthentication.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@WebServlet("/RegistrationServlet")
public class RegistrationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private String url = "jdbc:mysql://localhost:3306/userdetails";
    private String username = "root";
    private String password = "Omkar@123";

    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException{
        setCorsHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        setCorsHeaders(response);
        try{

            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;

            while ((line = reader.readLine()) != null){
                sb.append(line);
            }
            Gson gson = new Gson();
            User user = gson.fromJson(sb.toString(), User.class);

            if (!user.getPassword().equals(user.getRetypepassword())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Passwords do not match");
                return;
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, username,password);

            String sql = "INSERT INTO users (name, email, username, password, retypepassword) VALUES (? , ? , ? , ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getUsername());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getRetypepassword());

            int rowsInserted = stmt.executeUpdate();

            if(rowsInserted > 0){
                response.setStatus(HttpServletResponse.SC_OK);
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    public void setCorsHeaders(HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    public class User{
        private String name;
        private String email;
        private String username;
        private String password;
        private String retypepassword;

        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getRetypepassword(){return retypepassword;}

    }

}
