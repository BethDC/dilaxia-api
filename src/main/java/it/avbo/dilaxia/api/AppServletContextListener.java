package it.avbo.dilaxia.api;

import it.avbo.dilaxia.api.database.DBWrapper;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.sql.SQLException;

public class AppServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent contextEvent) {
        try {
            DBWrapper.setupDatabase();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
