package de.doridian.yiffbukkit.login;

import de.doridian.yiffbukkit.util.Configuration;

import java.net.SocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoriLogin {
    private static String connStr = null;
    private static String connUsr = null;
    private static String connPwd = null;
	private static Connection getConnection() throws SQLException {
        if(connStr == null) {
            connStr = "jdbc:mysql://" + Configuration.getValue("mysql-host", "localhost:3306") + "/" + Configuration.getValue("mysql-database", "test");
            connUsr = Configuration.getValue("mysql-user", "root");
            connPwd = Configuration.getValue("mysql-password", "");
        }
		return DriverManager.getConnection(connStr, connUsr, connPwd);
	}

	public static String verifyLogin(SocketAddress ip) {
		try {
			PreparedStatement pstmt = getConnection().prepareStatement("SELECT `username` FROM `logincookies` WHERE `ip`=?");
			pstmt.setString(1, ip.toString());

			ResultSet resultSet = pstmt.executeQuery();

			return resultSet.getString(1);
		}
		catch (SQLException exception) {
			return null;
		}
	}

	private static String saltPassword(String name, String password) {
		return name+" yiff "+password+" bukkit";
	}
	
	public static boolean checkPassword(String name, String password) {
		try {
			PreparedStatement pstmt = getConnection().prepareStatement("SELECT `name` FROM `users` WHERE `name`=? AND `password`=MD5(?)");
			pstmt.setString(1, name);
			pstmt.setString(2, saltPassword(name, password));
			ResultSet resultSet = pstmt.executeQuery();

			return resultSet.getString(1) == name;
		}
		catch (SQLException exception) {
            exception.printStackTrace();
			return false;
		}
	}

	public static boolean setPassword(String name, String password) {
		try {
			PreparedStatement pstmt = getConnection().prepareStatement("REPLACE INTO `users` (`name`, `password`) VALUES (?, MD5(?))");
			pstmt.setString(1, name);
			pstmt.setString(2, saltPassword(name, password));
			int changed = pstmt.executeUpdate();

			return changed > 0;
		}
		catch (SQLException exception) {
            exception.printStackTrace();
			return false;
		}
	}
}
