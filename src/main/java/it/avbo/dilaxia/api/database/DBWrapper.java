package it.avbo.dilaxia.api.database;

import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBWrapper {
    static protected final Connection connection;
    static private final MariaDbDataSource dataSource;

    static {
        dataSource = new MariaDbDataSource();
        try {
            dataSource.setUrl("jdbc:mariadb://localhost:3306/dilaxia");

            dataSource.setUser("root");
            dataSource.setPassword("");
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setupDatabase() {
        String dbInitialization = """
            CREATE OR REPLACE TABLE users (
                username VARCHAR(30) PRIMARY KEY,
                email VARCHAR(50) NOT NULL UNIQUE,
                password_hash VARBINARY(256) NOT NULL,
                salt VARBINARY(64) NOT NULL,
                role TINYINT NOT NULL
            );
           
           CREATE OR REPLACE TABLE tournaments (
               id INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY,
               description VARCHAR(255)
           );
           
           """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(dbInitialization);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
