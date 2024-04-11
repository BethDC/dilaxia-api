package it.avbo.dilaxia.api.database;

import it.avbo.dilaxia.api.entities.User;
import it.avbo.dilaxia.api.models.auth.enums.UserRole;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.*;
import java.util.Optional;

public class DBWrapper {
    static protected final Connection connection;
    static private final MariaDbDataSource dataSource;

    static {
        dataSource = new MariaDbDataSource();
        try {
            dataSource.setUrl("jdbc:mariadb://localhost:3306/dilaxia");

        dataSource.setUser("root");
        dataSource.setPassword("dilaxia");

        connection = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setupDatabase() throws SQLException {
        String dbInitialization = """
        CREATE OR REPLACE TABLE users (
            username VARCHAR(30) PRIMARY KEY,
            email VARCHAR(50) NOT NULL UNIQUE,
            password_hash VARBINARY(256) NOT NULL,
            salt VARBINARY(64) NOT NULL,
            role TINYINT NOT NULL
        );
        """;

        try(Statement statement = connection.createStatement()) {
            statement.execute(dbInitialization);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
