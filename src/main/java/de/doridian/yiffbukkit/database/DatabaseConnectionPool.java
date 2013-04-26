package de.doridian.yiffbukkit.database;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.StackKeyedObjectPoolFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionPool {
	private static DataSource dataSource;

    public static DatabaseConnectionPool instance = new DatabaseConnectionPool();

    private DatabaseConnectionPool() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch(Exception e) {
			System.err.println("Error loading JBBC MySQL: " + e.toString());
		}

		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory("jdbc:mysql://" + DatabaseConfiguration.HOST + "/" + DatabaseConfiguration.NAME, DatabaseConfiguration.USER, DatabaseConfiguration.PASSWORD);
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
				connectionFactory,
				new GenericObjectPool(),
				new StackKeyedObjectPoolFactory(),
				"SELECT 1+1",
				false,
				true
		);

		dataSource = new PoolingDataSource(new GenericObjectPool(poolableConnectionFactory));
    }

    public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
    }
}
