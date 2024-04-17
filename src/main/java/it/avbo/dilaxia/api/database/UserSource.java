package it.avbo.dilaxia.api.database;

import it.avbo.dilaxia.api.entities.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserSource {
    public boolean addUser(User user) {
        try(PreparedStatement statement = DBWrapper.connection.prepareStatement("""
            INSERT INTO utenti(username, email, sesso, data_nascita, ruolo, password_hash, salt)
           VALUES (?, ?, ?, ?, ?, ?, ?);
        """)) {
            statement.setString(1, user.username);
            statement.setString(2, user.email);
            statement.setString(3, String.valueOf(user.sex));
            statement.setDate(4, user.birthday);
            statement.setString(5, String.valueOf(user.role.getValue()));
            statement.setBytes(6, user.passwordHash);
            statement.setBytes(7, user.salt);
        } catch (SQLException e) {
            return false;
        }
    }
}
