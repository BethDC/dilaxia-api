package it.avbo.dilaxia.api.database;

import it.avbo.dilaxia.api.entities.User;
import it.avbo.dilaxia.api.models.auth.enums.UserRole;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.*;
import java.util.Optional;

public class DBWrapper {
    static private final Connection dbConnection;
    static private final MariaDbDataSource dataSource;
    static {
        try {
            dataSource = new MariaDbDataSource();
            dataSource.setUrl("jdbc:mariadb://localhost:3306/dilaxia");
            dataSource.setUser("root");
            dataSource.setPassword(null);

            dbConnection = dataSource.getConnection();
            //setupDatabase();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<User> getUserByUsername(@NonNull String username) {
        try(PreparedStatement statement = dbConnection.prepareStatement("""
            SELECT *
            FROM users
            WHERE users.username = ?
        """)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return Optional.of(
                        new User(
                                resultSet.getString("username"),
                                resultSet.getString("email"),
                                UserRole.fromValue(resultSet.getInt("role")),
                                resultSet.getBytes("password_hash"),
                                resultSet.getBytes("salt")
                        )
                );
            }
            return Optional.empty();

        } catch (SQLException e) {
            return Optional.empty();
        }
    }
    public static boolean addUser(@NonNull User user) {
        try(PreparedStatement statement = dbConnection.prepareStatement("""
            INSERT INTO users(username, email, password_hash, salt, role)
            values (?,?,?,?,?)
        """)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setBytes(3, user.getPasswordDigest());
            statement.setBytes(4, user.getSalt());
            statement.setInt(5, user.getRole().getValue());
            statement.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static void setupDatabase() {
        String dbInitialization = """
            USE dilaxia;
            DROP TABLE IF EXISTS users;
            CREATE TABLE users (
                username VARCHAR(30) PRIMARY KEY,
                email VARCHAR(50) NOT NULL UNIQUE,
                password_hash VARBINARY(256) NOT NULL,
                salt VARBINARY(64) NOT NULL,
                user_role TINYINT NOT NULL
            );
        """;
        try(Statement statement = dbConnection.createStatement()) {
            statement.executeUpdate(dbInitialization);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
