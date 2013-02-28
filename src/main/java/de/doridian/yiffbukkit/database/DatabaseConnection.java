package de.doridian.yiffbukkit.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnection {
    private Connection conn;
    private final DatabaseConnectionPool pool;
    protected DatabaseConnection(DatabaseConnectionPool pool) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://" + DatabaseConfiguration.HOST + "/" + DatabaseConfiguration.NAME, DatabaseConfiguration.USER, DatabaseConfiguration.PASSWORD);
        } catch(Exception e) {
            System.err.println("Error connecting to DB: " + e.toString());
        }
        use();
        this.pool = pool;
    }

    protected long lastUsed = 0;
    protected void use() {
        lastUsed = System.currentTimeMillis();
    }
    protected long getLastUsed() {
        return lastUsed;
    }

    protected void close() {
        try {
            conn.close();
        } catch(SQLException e) {
            System.err.println("SQL Exception: " + e.toString());
        }
    }

    public void free() {
        pool.freeDatabaseConnection(this);
    }

    public PreparedStatement createPreparedStatement(String sql) {
        return createPreparedStatement(sql, 0);
    }

    public PreparedStatement createPreparedStatement(String sql, int parameters) {
        try {
            return conn.prepareStatement(sql, parameters);
        } catch(SQLException e) {
            return null;
        }
    }
}
