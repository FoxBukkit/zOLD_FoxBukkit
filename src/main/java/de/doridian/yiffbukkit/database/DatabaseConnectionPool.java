package de.doridian.yiffbukkit.database;

import de.doridian.yiffbukkit.main.util.Configuration;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.StackKeyedObjectPoolFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionPool {
	private static PoolingDataSource dataSource;

    public static DatabaseConnectionPool instance = new DatabaseConnectionPool();

    private DatabaseConnectionPool() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch(Exception e) {
			System.err.println("Error loading JBBC MySQL: " + e.toString());
		}

		GenericObjectPool connectionPool = new GenericObjectPool(null);
		connectionPool.setMaxActive(10);
		connectionPool.setMaxIdle(5);
		connectionPool.setTestOnBorrow(true);
		connectionPool.setTestOnReturn(true);
		connectionPool.setTestWhileIdle(true);

		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(Configuration.getValue("database-uri", ""), Configuration.getValue("database-user", ""), Configuration.getValue("database-password", ""));
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
				connectionFactory,
				connectionPool,
				new StackKeyedObjectPoolFactory(),
				"SELECT 1",
				false,
				true
		);
		poolableConnectionFactory.setValidationQueryTimeout(3);

		dataSource = new PoolingDataSource(connectionPool);
    }

    public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
    }
}
