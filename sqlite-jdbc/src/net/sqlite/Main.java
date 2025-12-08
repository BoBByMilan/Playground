package net.sqlite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        connect();
        initTables();
        loadData();
    }

    static String url = "jdbc:sqlite:c:/sql3/playground.db";


    public static void connect() {

        try (Connection conn = DriverManager.getConnection(url)) {

            DatabaseMetaData meta = conn.getMetaData();

            if (conn != null) {
                System.out.println("The database name is : " + meta.getDatabaseProductName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void initTables() {

        String[] sqlCommands = {
                "CREATE TABLE IF NOT EXISTS Users (id INTEGER PRIMARY KEY, name TEXT NOT NULL);)", // Vannak a felhasználók akik kaphatnak és indíthatnak tranzakciót
                "CREATE TABLE IF NOT EXISTS Accounts (id INTEGER PRIMARY KEY, user_id INTEGER, balance DOUBLE);)", // A felhasználóknak vannak számláik
                "CREATE TABLE IF NOT EXISTS Transactions (id INTEGER PRIMARY KEY, amount DOUBLE, account_id INTEGER, merchant_id INTEGER, timestamp INTEGER);)",
                "CREATE TABLE IF NOT EXISTS Fraud_logs(id INTEGER, trans_id INTEGER, risk_score DOUBLE, thread_name TEXT)",  //
                "CREATE TABLE IF NOT EXISTS Merchants(id INTEGER PRIMARY KEY, name TEXT, category TEXT, risk_level INTEGER)", // Legyen egy (kereskedő)eladó akinek van megbízhatósági szintje(risk_level), miket ad el (category)
                "CREATE TABLE IF NOT EXISTS User_Profiles (account_id INTEGER PRIMARY KEY, avg_spendings DOUBLE, total_transactions INTEGER,total_spendings DOUBLE)"
        };

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();

            for (String sql : sqlCommands) {
                stmt.execute(sql);
            }
            System.out.println(sqlCommands.length);
            System.out.println("Tables created!");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<TransactionContext> loadData(){
        List<TransactionContext> list = new ArrayList<>();
        //Textblock
        String query = """
                SELECT 
                    t.id AS trans_id, 
                    t.amount, 
                    m.risk_level, 
                    m.category, 
                    u.avg_spendings,
                    u.total_transactions,
                    u.total_spendings
                FROM Transactions t
                JOIN Merchants m ON t.merchant_id = m.id
                JOIN User_Profiles u ON t.account_id = u.account_id
                LIMIT 500000
                """;

        try(Connection conn = DriverManager.getConnection(url);
           Statement stmt = conn.createStatement();
           ResultSet rs = stmt.executeQuery(query)){

            while (rs.next()) {
                list.add(new TransactionContext(
                        rs.getInt("trans_id"),
                        rs.getDouble("amount"),
                        rs.getInt("risk_level"),
                        rs.getString("category"),
                        rs.getDouble("avg_spendings"),
                        rs.getDouble("total_spendings"),
                        rs.getInt("total_transactions")
                ));
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public static void generateDataForTables(){

    }
}