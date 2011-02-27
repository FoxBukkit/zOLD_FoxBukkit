package de.doridian.yiffbukkit.login;

import java.net.SocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoriLogin {
	private static Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/minecraftsql3", "root", "");
	}

	public static String verifyLogin(SocketAddress ip) {
		try
		{
			PreparedStatement pstmt = getConnection().prepareStatement("SELECT username FROM logincookies WHERE ip=?");
			pstmt.setString(1, ip.toString());

			ResultSet resultSet = pstmt.executeQuery();

			return resultSet.getString(1);
		}
		catch(SQLException exception)
		{
			return null;
		}
	}

	public static boolean setPassword(String name, String password) {
		try
		{
			PreparedStatement pstmt = getConnection().prepareStatement("REPLACE INTO users (name, password) VALUES (?, MD5(?))");
			pstmt.setString(1, name);
			pstmt.setString(1, saltPassword(name, password));
			int changed = pstmt.executeUpdate();

			return changed > 0;
		}
		catch(SQLException exception)
		{
			return false;
		}
	}

	public static String saltPassword(String name, String password) {
		return name+" yiff "+password+" bukkit";
	}

}
