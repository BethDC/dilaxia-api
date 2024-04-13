package it.avbo.dilaxia.api.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TournamentsSource {
    public static boolean addTournament(String description) {
        try(PreparedStatement statement = DBWrapper.connection.prepareStatement("""
            INSERT INTO tournaments(description)
            values (?);
        """)
        ){
            statement.setString(1, description);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
