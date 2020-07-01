package ru.pamishenko.server;

import java.sql.*;

public class AuthService {
    private static Connection connection;
    private static Statement stmt;

    public static void dbConnect () {
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:D:/Java/Chat3/MChat/src/main/resources/MyDB";
            connection = DriverManager.getConnection(url);
            stmt = connection.createStatement();
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }

    public static void dbDisconnect () {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static String getNameByLoginAndPass (String login, String password){
        try {
            ResultSet rs = stmt.executeQuery("SELECT id, login, password FROM users WHERE login = '" + login + "'");
            if (rs.next()) {
                String user_name = rs.getString("login");


                if(password.equals(rs.getString("password"))){
//                    System.out.println(rs.getString("password"));  // лог
//                    System.out.println("Проверка пароля: " + password.equals(rs.getString("password"))); // лог
                    return user_name;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}