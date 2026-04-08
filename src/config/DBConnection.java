package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DEFAULT_URL = "jdbc:sqlserver://LAPTOP-OFNPMO5R:1433;databaseName=UniDB;encrypt=false;trustServerCertificate=true";
    private static final String DEFAULT_USER = "vikum";
    private static final String DEFAULT_PASSWORD = "admin@123";

    public static Connection getConnection() throws SQLException {
        String url = getEnvOrDefault("DB_URL", DEFAULT_URL);
        String user = getEnvOrDefault("DB_USER", DEFAULT_USER);
        String password = getEnvOrDefault("DB_PASSWORD", DEFAULT_PASSWORD);

        return DriverManager.getConnection(url, user, password);
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }
}