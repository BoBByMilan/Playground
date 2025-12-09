package net.sqlite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 *
 * */

public class Main {

    static String url = "jdbc:sqlite:c:/sql3/playground.db";

    public static void main(String[] args) {
        connect();
        initTables();
        //generateDataForTables(100, 100_000); //RUN ONCE!!! (In development)

        List<TransactionContext> data = loadData();
        System.out.println("Sikeresen betöltve: " + data.size() + " sor.");

        if (!data.isEmpty()) {
            System.out.println("Első adat próbája: " + data.get(0));
        }
    }

//
//
//


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
                "CREATE TABLE IF NOT EXISTS Users (id INTEGER PRIMARY KEY, name TEXT NOT NULL)", // Vannak a felhasználók akik kaphatnak és indíthatnak tranzakciót
                "CREATE TABLE IF NOT EXISTS Accounts (id INTEGER PRIMARY KEY, user_id INTEGER, balance DOUBLE)", // A felhasználóknak vannak számláik
                "CREATE TABLE IF NOT EXISTS Transactions (id INTEGER PRIMARY KEY, amount DOUBLE, account_id INTEGER, merchant_id INTEGER, timestamp INTEGER)",
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

    public static List<TransactionContext> loadData() {
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

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

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
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public static void generateDataForTables(int merchantCount, int transactionsCount) {
        int numberOfUsers = 5_000;
        Random rand = new Random();

        System.out.println("Generating rows of data for tables...\n");

        String insertIntoUser = "INSERT INTO Users (name) VALUES (?)";
        String insertIntoAccount = "INSERT INTO Accounts (user_id, balance) VALUES (?, ?)";
        String insertIntoMerchants = "INSERT INTO Merchants (name, category, risk_level) VALUES (?, ?, ?)";
        String insertIntoUserProfiles = "INSERT INTO User_profiles (account_id, avg_spendings, total_transactions, total_spendings) VALUES (?, ?, ?, ?)";
        String insertIntoTransactions = "INSERT INTO Transactions (amount, account_id, merchant_id, timestamp) VALUES (?, ?, ?, ?)";


        System.out.println("Starting to generate data for Users ...\n");
        try (Connection conn = DriverManager.getConnection(url)) {
            conn.setAutoCommit(false);      //for speed we set the commit type to false

            try (PreparedStatement psUser = conn.prepareStatement(insertIntoUser);
                 PreparedStatement psAccount = conn.prepareStatement(insertIntoAccount);
                 PreparedStatement psUserProfiles = conn.prepareStatement(insertIntoUserProfiles);) {
                for (int i = 1; i <= numberOfUsers; i++) {
                    //Users
                    psUser.setString(1, "User_" + i);
                    psUser.addBatch();

                    //Accounts
                    psAccount.setInt(1, i);
                    psAccount.setDouble(2, rand.nextDouble() * 1_000_000); //starting balance
                    psAccount.addBatch();

                    //UserProfiles
                    psUserProfiles.setInt(1, i);
                    psUserProfiles.setDouble(2, 5000 + (rand.nextDouble() * 50000));
                    psUserProfiles.setInt(3, 0); //starting transactions
                    psUserProfiles.setDouble(4, 0.0); //starting spendings
                    psUserProfiles.addBatch();

                    if (i % 1000 == 0) {
                        psUser.executeBatch();
                        psAccount.executeBatch();
                        psUserProfiles.executeBatch();
                    }
                }
                psUser.executeBatch();
                psAccount.executeBatch();
                psUserProfiles.executeBatch();
            }
            System.out.println("Done generating data for Users!\n");


            System.out.println("Starting to generate data for Merchants ...\n");
            try (PreparedStatement ps = conn.prepareStatement(insertIntoMerchants)) {
                String[] categories = {"Food", "Electronics", "Gambling", "Travel", "Retail"};
                for (int i = 1; i <= merchantCount; i++) {
                    ps.setString(1, "Merchant_" + i);                // name (example:Merchant_5 ... )
                    ps.setString(2, categories[i % categories.length]); // category
                    ps.setInt(3, (i % 5) + 1);                       // risk level 1-5
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            System.out.println("Done generating data for Merchants!\n");


            System.out.println("Starting to generate data for Transactions ...\n");
            try (PreparedStatement ps = conn.prepareStatement(insertIntoTransactions)) {
                for (int i = 1; i <= transactionsCount; i++) {
                    ps.setDouble(1, 100 + (Math.random() * 500000)); //amount
                    ps.setInt(2,     rand.nextInt(numberOfUsers) + 1);   //random user ID (1-5000)
                    ps.setInt(3,     rand.nextInt(merchantCount) + 1);    //random merchant ID (1-merchant count)
                    ps.setLong(4, System.currentTimeMillis());          //timestamp
                    ps.addBatch();

                }
                ps.executeBatch();
            }
            System.out.println("Done generating data for Transactions!\n");
            System.out.println("Every data has been generated.\n");

            conn.commit();
            System.out.println("All data generated and SAVED successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}