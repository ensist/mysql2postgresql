package org.endrisiswanto.mysql2postgresql.main;

import org.endrisiswanto.mysql2postgresql.connection.Mysql;
import org.endrisiswanto.mysql2postgresql.connection.Postgresql;
import org.endrisiswanto.mysql2postgresql.io.IO;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Mysql2Postgresql {

    int SPLIT_RECORD = 30000;

    public Mysql2Postgresql() throws SQLException {
        List<String> listDoneTable = new ArrayList<>();
        for (String table : IO.readFile("success.txt").split("\n")) {
            listDoneTable.add(table);
        }
        for (String table : IO.readFile("failed.txt").split("\n")) {
            listDoneTable.add(table);
        }

        Connection mysql = Mysql.getConnection();
        DatabaseMetaData md = mysql.getMetaData();
        ResultSet rs = md.getTables(null, null, "%", null);
        while (rs.next()) {
            String table = rs.getString(3);
            if (!listDoneTable.contains(table)) {
                try {
                    copyTable(table, false);
                } catch (Exception e) {
                    copyTable(table, true);
                }
            }
        }
        rs.close();
        mysql.close();
    }

    void copyTable(String table, boolean singleCopy) throws SQLException {
        System.out.println("copying table " + table);

        Connection psql = Postgresql.getConnection();
        Statement psqlStatement = psql.createStatement();
        psqlStatement.executeUpdate("ALTER TABLE " + table + " DISABLE TRIGGER ALL");
        psqlStatement.executeUpdate("DELETE FROM " + table);

        int offset = 0, c = 0;
        while (true) {
            StringBuilder sql = new StringBuilder("SELECT * FROM ").append(table)
                    .append(" LIMIT ").append(offset).append(", ").append(SPLIT_RECORD);

            Connection mysql = Mysql.getConnection();
            Statement mysqlStatement = mysql.createStatement();
            ResultSet rs = mysqlStatement.executeQuery(sql.toString());

            if (!rs.next()) {
                break;
            }
            sql = new StringBuilder("INSERT INTO ").append(table).append(" VALUES");
            do {
                if (c % SPLIT_RECORD > 0 && !singleCopy) {
                    sql.append(",");
                }
                sql.append("(");
                final int columnCount = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) {
                        sql.append(",");
                    }
                    if (rs.getString(i) == null) {
                        sql.append("null");
                    } else {
                        sql.append("'").append(convertToPostgresql(rs.getString(i))).append("'");
                    }
                }
                sql.append(")");
                System.out.println(c + " record from table " + table + " copied");
                c++;

                if (singleCopy) {
                    System.out.println("executing query...");
                    try {
                        psqlStatement.executeUpdate(sql.toString());
                    } catch (Exception e) {
                    }
                    sql = new StringBuilder("INSERT INTO ").append(table).append(" VALUES");
                }
            } while (rs.next());

            mysqlStatement.close();
            rs.close();
            mysql.close();

            if (!singleCopy) {
                System.out.println("executing query...");
                try {
                    psqlStatement.executeUpdate(sql.toString());
                } catch (Exception e) {
                    IO.write("failed.txt", table, true);
                    throw e;
                }
            }
            offset += SPLIT_RECORD;
        }

        psqlStatement.executeUpdate("ALTER TABLE " + table + " ENABLE TRIGGER ALL");
        psqlStatement.close();
        psql.close();

        System.out.println("execute query done");
        IO.write("success.txt", table, true);
    }

    String convertToPostgresql(String value) {
        value = value.replaceAll("'", "''");
        value = value.replaceAll("" + (char) 0, " ");
        return value;
    }

    public static void main(String[] args) throws SQLException {
        new Mysql2Postgresql();
    }
}
