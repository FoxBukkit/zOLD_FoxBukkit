package de.doridian.yiffbukkit.database;

import de.doridian.yiffbukkit.main.config.Configuration;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.StackKeyedObjectPoolFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(Configuration.getValue("database-uri", "jdbc:mysql://localhost:3306/yiffbukkit_database"), Configuration.getValue("database-user", "root"), Configuration.getValue("database-password", "password"));
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

		try {
			initialize();
		} catch (SQLException exc) {
			System.err.println("Error initializing MySQL Database");
			exc.printStackTrace();
		}
    }

	public void initialize() throws SQLException {
		Connection connection = getConnection();
		_runSQL(connection, "CREATE TABLE IF NOT EXISTS `players` (\n" +
							"  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
							"  `name` varchar(32) NOT NULL,\n" +
							"  `uuid` varchar(128) NOT NULL DEFAULT '',\n" +
							"  PRIMARY KEY (`id`),\n" +
							"  UNIQUE KEY `uuid` (`uuid`),\n" +
							"  KEY `name` (`name`)\n" +
							") ENGINE=InnoDB;");
		_runSQL(connection, "CREATE TABLE IF NOT EXISTS `bans` (\n" +
							"  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
							"  `player` int(11) NOT NULL,\n" +
							"  `admin` int(11) NOT NULL,\n" +
							"  `reason` varchar(255) NOT NULL,\n" +
							"  `type` enum('global','local','temp') NOT NULL,\n" +
							"  `time` int(11) NOT NULL,\n" +
							"  PRIMARY KEY (`id`),\n" +
							"  UNIQUE KEY `player` (`player`),\n" +
							"  KEY `admin` (`admin`),\n" +
							"  CONSTRAINT `bans_player_fk` FOREIGN KEY (`player`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
							"  CONSTRAINT `bans_admin_fk` FOREIGN KEY (`admin`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
							") ENGINE=InnoDB;");
		_runSQL(connection, "CREATE TABLE IF NOT EXISTS `player_ips` (\n" +
							"  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
							"  `player` int(11) NOT NULL,\n" +
							"  `ip` varbinary(64) NOT NULL,\n" +
							"  `time` int(11) NOT NULL,\n" +
							"  PRIMARY KEY (`id`),\n" +
							"  UNIQUE KEY `player_ip` (`player`,`ip`),\n" +
							"  KEY `player` (`player`),\n" +
							"  KEY `ip` (`ip`),\n" +
							"  CONSTRAINT `ips_player_fk` FOREIGN KEY (`player`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
							") ENGINE=InnoDB;\n");
		connection.close();
	}

	private void _runSQL(Connection connection, String sql) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.execute();
		stmt.close();
	}

    public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
    }
}
