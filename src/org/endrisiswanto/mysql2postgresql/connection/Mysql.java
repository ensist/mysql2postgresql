package org.endrisiswanto.mysql2postgresql.connection;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class Mysql {

    static String HOST = "127.0.0.1";
    static String DATABASE = "database";
    static String PORT = "3306";
    static String USERNAME = "root";
    static String PASSWORD = "password";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?zeroDateTimeBehavior=convertToNull",
                    USERNAME,
                    PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return connection;
    }

}
