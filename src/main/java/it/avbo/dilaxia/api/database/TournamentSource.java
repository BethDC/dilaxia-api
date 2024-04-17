package it.avbo.dilaxia.api.database;

import it.avbo.dilaxia.api.entities.Tournament;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class TournamentSource {

    public static Optional<Tournament> getTournamentById(Integer id) {
        try (PreparedStatement statement = DBWrapper.getConnection().prepareStatement("""
                   SELECT *
                   FROM tornei
                   WHERE id = ?
                    LIMIT 1;
                """)) {
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return Optional.of(new Tournament(
                        result.getInt("id"),
                        result.getInt("id_sport"),
                        result.getInt("id_campo"),
                        result.getString("coach"),
                        result.getString("prof_creatore"), result.getString("descrizione")
                ));
            }
        } catch (SQLException ignored) {
        }
        return Optional.empty();
    }

    public static boolean addTournament(Tournament tournament) {
        try (PreparedStatement statement = DBWrapper.getConnection().prepareStatement("""
                    INSERT INTO tornei(id_sport, id_campo, coach, prof_creatore, descrizione)
                    values (?, ?, ?, ?, ?);
                """)
        ) {
            statement.setInt(1, tournament.sportId);
            statement.setInt(2, tournament.campId);
            statement.setString(3, tournament.coach);
            statement.setString(4, tournament.profCreator);
            statement.setString(5, tournament.description);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
