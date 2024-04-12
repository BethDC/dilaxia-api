package it.avbo.dilaxia.api.database;

import it.avbo.dilaxia.api.entities.User;
import it.avbo.dilaxia.api.models.auth.enums.UserRole;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UsersSource {

    public static Optional<User> getUserByIdentifier(@NonNull String identifier) {
        try(PreparedStatement statement = DBWrapper.connection.prepareStatement("""
          SELECT *
          FROM users
          WHERE users.username = ? OR users.email = ?
          LIMIT 1
      """)) {
            statement.setString(1, identifier);
            statement.setString(2, identifier);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new User(
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        UserRole.fromValue(resultSet.getInt("role")),
                        resultSet.getBytes("password_hash"),
                        resultSet.getBytes("salt")
                ));
            }
            return Optional.empty();
        }catch (SQLException e) {
            return Optional.empty();
        }
    }



    public static boolean addUser(@NonNull User user) {
        try(PreparedStatement statement = DBWrapper.connection.prepareStatement("""
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

}
