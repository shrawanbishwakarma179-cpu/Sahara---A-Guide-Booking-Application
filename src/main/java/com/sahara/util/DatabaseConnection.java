package com.sahara.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * DatabaseConnection Class
 * ------------------------
 * This class manages the connection between the Java application and MySQL database.
 * 
 * Design Pattern:
 * → Singleton Pattern (only one instance of this class is created)
 * 
 * Responsibilities:
 * → Load database configuration from db.properties file
 * → Establish connection with MySQL
 * → Provide connection to DAO classes
 * → Check connection status
 * → Close connection when application stops
 */
public class DatabaseConnection {

    // Static instance (Singleton)
    private static DatabaseConnection instance;

    // Connection object used to interact with database
    private Connection connection;

    // Database configuration variables
    private String url;
    private String username;
    private String password;

    /**
     * Private constructor
     * → Prevents object creation from outside
     * → Loads properties and establishes connection
     */
    private DatabaseConnection() {
        try {
            // Create Properties object to read configuration file
            Properties props = new Properties();

            // Load db.properties file from resources folder
            InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties");

            // If file is not found, throw error
            if (in == null) {
                throw new RuntimeException("db.properties file NOT found!");
            }

            // Load properties into memory
            props.load(in);

            // Read database configuration values
            url = props.getProperty("db.url");
            username = props.getProperty("db.username");
            password = props.getProperty("db.password");

            // Establish connection using DriverManager
            connection = DriverManager.getConnection(url, username, password);

            // Print success message
            System.out.println("[SAHARA] Database CONNECTED successfully.");

        } catch (Exception e) {
            // If connection fails, print error
            System.err.println("[SAHARA] Database CONNECTION FAILED!");
            e.printStackTrace();
        }
    }

    /**
     * Returns the single instance of DatabaseConnection
     * (Singleton access method)
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Returns the active database connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Checks whether the database is connected or not
     * 
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Closes the database connection safely
     * (used when application stops)
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[SAHARA] Database connection closed.");
            }
        } catch (Exception e) {
            System.err.println("[SAHARA] Error closing connection: " + e.getMessage());
        }
    }
}