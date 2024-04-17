package it.avbo.dilaxia.api.servlets.tournaments;

import it.avbo.dilaxia.api.database.TournamentSource;
import it.avbo.dilaxia.api.entities.enums.UserRole;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/tournaments/create")
public class CreateTournament extends HttpServlet {
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserRole role = (UserRole) req.getSession(false).getAttribute("role");
        if(role == null) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
        /*
        if(!TournamentSource.addTournament("dfwa")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        }*/
        resp.setStatus(HttpServletResponse.SC_CREATED);

    }
}
