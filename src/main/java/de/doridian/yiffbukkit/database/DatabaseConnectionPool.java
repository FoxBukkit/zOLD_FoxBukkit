package de.doridian.yiffbukkit.database;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DatabaseConnectionPool {
    public static DatabaseConnectionPool instance = new DatabaseConnectionPool();

    private Queue<DatabaseConnection> freeConnections = new ConcurrentLinkedQueue<DatabaseConnection>();
    private ArrayList<DatabaseConnection> allConnections = new ArrayList<DatabaseConnection>();

    private boolean isAlive = true;

    private DatabaseConnectionPool() {
        new Thread() {
            public void run() {
                while(isAlive) {
                    int currentlySpare = freeConnections.size();
                    int currentlyTotal = allConnections.size();
                    long curTime = System.currentTimeMillis();
                    if(currentlySpare > DatabaseConfiguration.MIN_SPARE_CONNECTIONS && currentlyTotal > DatabaseConfiguration.MIN_CONNECTIONS) {
                        ArrayList<DatabaseConnection> currentFree = new ArrayList<DatabaseConnection>(freeConnections);
                        for(DatabaseConnection connection : currentFree) {
                            if(currentlySpare > DatabaseConfiguration.MAX_SPARE_CONNECTIONS || curTime - connection.getLastUsed() > DatabaseConfiguration.TIMEOUT_MS) {
                                currentlySpare--;
                                if(closeDatabaseConnection(connection)) {
                                    currentlyTotal--;
                                }
                                System.out.println("Database pool idle watcher has killed a connection");
                                if(currentlySpare <= DatabaseConfiguration.MIN_SPARE_CONNECTIONS || currentlyTotal <= DatabaseConfiguration.MIN_CONNECTIONS) {
                                    break;
                                }
                            }
                        }
                    }

                    try {
                        Thread.sleep(1000);
                    } catch(Exception e) {

                    }
                }
            }
        }.start();

        for(int i = 0; i < DatabaseConfiguration.MIN_CONNECTIONS; i++) {
            DatabaseConnection connection = new DatabaseConnection(this);
            allConnections.add(connection);
            freeConnections.add(connection);
        }

        System.out.println("DB pool initiated");
    }

    public void close() {
        isAlive = false;
        freeConnections = null;
        for(DatabaseConnection connection : allConnections) {
            connection.close();
        }
        allConnections = null;
    }

    protected void freeDatabaseConnection(DatabaseConnection connection) {
        freeConnections.add(connection);
    }

    protected boolean closeDatabaseConnection(DatabaseConnection connection) {
        if(!freeConnections.remove(connection))
            return false;
        allConnections.remove(connection);

        connection.close();
        return true;
    }

    public DatabaseConnection getConnection() {
        DatabaseConnection connection = freeConnections.poll();
        if(connection == null) {
            if(allConnections.size() < DatabaseConfiguration.MAX_CONNECTIONS) {
                connection = new DatabaseConnection(this);
                allConnections.add(connection);
                System.out.println("Creating new database connection...");
            } else {
                System.out.println("Cannot make new connection (MAX_CONNECTIONS reached). Waiting...");
                while((connection = freeConnections.poll()) == null) {
                    try {
                        Thread.sleep(100);
                    } catch(Exception e) {

                    }
                }
            }
        } else {
            connection.use();
        }
        return connection;
    }
}
