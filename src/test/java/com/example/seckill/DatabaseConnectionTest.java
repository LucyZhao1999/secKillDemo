package com.example.seckill;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/seckill";
        String username = "root";
        String password = "1234aaaa";

        try {
            // 加载数据库驱动程序
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 建立数据库连接
            Connection connection = DriverManager.getConnection(url, username, password);

            // 连接成功
            System.out.println("Database connection successful.");

            // 关闭数据库连接
            connection.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Database driver not found.");
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
        }
    }
}
