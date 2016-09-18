package org.endrisiswanto.mysql2postgresql.connection;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class Postgresql {

    static String HOST = "127.0.0.1";
    static String DATABASE = "database";
    static String PORT = "5432";
    static String USERNAME = "postgres";
    static String PASSWORD = "password";

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE + "?zeroDateTimeBehavior=convertToNull",
                    USERNAME,
                    PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return connection;
    }

}
