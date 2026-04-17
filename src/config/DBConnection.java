package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBConnection {

    private static final String DEFAULT_URL = "jdbc:sqlserver://LAPTOP-OFNPMO5R:1433;databaseName=UniDB;encrypt=false;trustServerCertificate=true";
    private static final Path DOT_ENV_PATH = Path.of(".env");
    private static final Map<String, String> DOT_ENV_VALUES = loadDotEnv();

    public static Connection getConnection() throws SQLException {
        String url = getEnvOrDefault("DB_URL", DEFAULT_URL);
        String user = getRequiredEnv("DB_USER");
        String password = getRequiredEnv("DB_PASSWORD");

        return DriverManager.getConnection(url, user, password);
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = getEnvValue(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    private static String getRequiredEnv(String key) {
        String value = getEnvValue(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(key + " is required. Set it in OS environment variables or in a .env file.");
        }
        return value.trim();
    }

    private static String getEnvValue(String key) {
        String fromSystem = System.getenv(key);
        if (fromSystem != null && !fromSystem.trim().isEmpty()) {
            return fromSystem;
        }
        return DOT_ENV_VALUES.get(key);
    }

    private static Map<String, String> loadDotEnv() {
        Map<String, String> values = new HashMap<>();
        if (!Files.exists(DOT_ENV_PATH)) {
            return values;
        }

        try {
            List<String> lines = Files.readAllLines(DOT_ENV_PATH);
            for (String rawLine : lines) {
                String line = rawLine.trim();
                if (line.isEmpty() || line.startsWith("#") || !line.contains("=")) {
                    continue;
                }

                int separatorIndex = line.indexOf('=');
                String key = line.substring(0, separatorIndex).trim();
                String value = line.substring(separatorIndex + 1).trim();

                if (!key.isEmpty()) {
                    values.put(key, value);
                }
            }
        } catch (Exception e) {
             throw new RuntimeException("Failed to load .env file", e);
        }

        return values;
    }
}