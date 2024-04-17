package it.avbo.dilaxia.api.database;

import org.mariadb.jdbc.MariaDbDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBWrapper {
    static private Connection connection;
    static private final MariaDbDataSource dataSource;

    static {
        dataSource = new MariaDbDataSource();
        try {
            dataSource.setUrl("jdbc:mariadb://localhost:3306/dilaxia?allowMultiQueries=true&createDatabaseIfNotExist=true");

            dataSource.setUser("root");
            dataSource.setPassword("");
            connection = dataSource.getConnection();
        } catch (SQLException ignored) {
            try {
                dataSource.setPassword("dilaxia");
                connection = dataSource.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void setupDatabase() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        String dbInitialization;
        try(InputStream inputStream = classloader.getResourceAsStream("init.sql")) {
            assert inputStream != null;
            dbInitialization = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute(dbInitialization);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
