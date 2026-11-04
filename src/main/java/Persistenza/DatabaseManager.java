package Persistenza;

import java.sql.*;

public class DatabaseManager {
    private Connection connection = null;
    private static DatabaseManager instance = null;

    private boolean testConnection ()
    {
        try {
            return connection.isValid(5);
        } catch (SQLException e)
        {
            return false;
        }
    }

    private DatabaseManager(String url) throws SQLException {
        connection = DriverManager.getConnection(url);
    }

    public static DatabaseManager getInstance() throws SQLException {
        if (instance == null)
        {
            instance = new DatabaseManager("jdbc:sqlite:rossigo.db");
        }
        return instance;
    }


    public void insert()
    {

    }
}