package de.doridian.yiffbukkit.login;

import java.net.SocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.minecraft.server.NetLoginHandler;
import net.minecraft.server.Packet1Login;

public class ThreadDoriLoginVerifier extends Thread {
	final NetLoginHandler loginHandler;
	final Packet1Login loginPacket;

	public ThreadDoriLoginVerifier(NetLoginHandler loginHandler, Packet1Login loginPacket) {
		this.loginHandler = loginHandler;
		this.loginPacket = loginPacket;
	}
	

	@Override
	public void run() {
		SocketAddress ip = loginHandler.b.b();

		/*
		URL url = new URL("http://mc.doridian.de/validate.php?ip="+ip);
		BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
		String result = bufferedreader.readLine();
		bufferedreader.close();
		*/

		try
		{
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/minecraftsql3", "root", "");
			Statement stmt = con.createStatement();

			ResultSet resultSet = stmt.executeQuery("SELECT username FROM logincookies WHERE ip=\"" + ip + "\"");

			loginHandler.g = resultSet.getString(1);
			NetLoginHandler.a(loginHandler, loginPacket);

		}
		catch(SQLException exception)
		{
			loginHandler.a("Failed to verify username!");
		}
	}

}
