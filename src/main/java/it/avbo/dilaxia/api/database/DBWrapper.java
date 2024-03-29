package it.avbo.dilaxia.api.database;

import it.avbo.dilaxia.api.entities.User;
import it.avbo.dilaxia.api.models.auth.enums.UserRole;

import java.sql.*;
import java.util.Optional;

public class DBWrapper {
    static private final Connection dbConnection;

    static {
        try {
            dbConnection = DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/dilaxia",
                    "root", ""
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<User> getUserByUsername(String username) {
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
    public static boolean addUser(User user) {
        try(PreparedStatement statement = dbConnection.prepareStatement("""
            INSERT INTO users(username, email, password_hash, salt, role)
            values (?,?,?,?,?)
        """)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setBytes(3, user.getPasswordDigest());
            statement.setBytes(4, user.getSalt());
            statement.setInt(5, user.getRole().getValue());
            return statement.execute();
        } catch (SQLException e) {
            return false;
        }
    }

    public static void setupDatabase() {
        try(PreparedStatement statement = dbConnection.prepareStatement("""
            USING dilaxia;
            
            CREATE TABLE users (
                VARCHAR(30) username PRIMARY_KEY,
                VARCHAR(50) email UNIQUE NOT NULL,
                VARBINARY(256) password_hash NOT NULL,
                VARBINARY(64) salt NOT NULL,
                TINYINT role NOT NULL 
            );
        """)) {
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
