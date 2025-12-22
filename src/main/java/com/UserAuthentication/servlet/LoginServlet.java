package com.UserAuthentication.servlet;

import com.google.gson.Gson;
import com.mysql.cj.protocol.Resultset;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private final String url = "jdbc:mysql://localhost:3306/userdetails";
    private final String username = "root";
    private final String password = "Omkar@123";

    public void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setCorsheader(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        setCorsheader(response);
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader bf = request.getReader();
            String line;
            while((line = bf.readLine()) != null) sb.append(line);

            Gson gson = new Gson();
            LoginRequest loginRequest = gson.fromJson(sb.toString(), LoginRequest.class);

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);

            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, loginRequest.getUsername());
            ResultSet rs = stmt.executeQuery();

            if(!rs.next()){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("❌ You are not registered");
            }else{
                //user exists, check password
                String StoredPassword = rs.getString("password");
                if(StoredPassword.equals(loginRequest.getPassword())){
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("✅ Login successful");
                }else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("❌ Incorrect password");
                }
            }
            rs.close();
            conn.close();
            stmt.close();


        }catch (Exception e){

            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    public void setCorsheader(HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
    public class LoginRequest {
        public String username;
        public String password;

        public String getUsername(){
            return username;
        }
        public String getPassword(){
            return password;
        }
    }
}
