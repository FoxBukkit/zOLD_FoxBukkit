package de.doridian.yiffbukkit.database;

public class DatabaseConfiguration {
	protected static final String NAME = "doribans";
	protected static final String USER = "doribans";
	protected static final String HOST = "localhost:3306";
	protected static final String PASSWORD = "SECRET";

    protected static final int MIN_CONNECTIONS = 1;
    protected static final int MAX_CONNECTIONS = 20;
    protected static final long TIMEOUT_MS = 10000;
    protected static final int MIN_SPARE_CONNECTIONS = 1;
    protected static final int MAX_SPARE_CONNECTIONS = 6;
}
