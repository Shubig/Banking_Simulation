package com.bankingsim.dao;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;

public class DbUtil {
    private static String url;
    private static String user;
    private static String pass;

    static {
        try (InputStream in = DbUtil.class.getResourceAsStream("/db.properties")) {
            Properties p = new Properties();
            p.load(in);
            url = p.getProperty("jdbc.url");
            user = p.getProperty("jdbc.user");
            pass = p.getProperty("jdbc.password");
            Class.forName(p.getProperty("jdbc.driver","com.mysql.cj.jdbc.Driver"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load DB properties: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
}
