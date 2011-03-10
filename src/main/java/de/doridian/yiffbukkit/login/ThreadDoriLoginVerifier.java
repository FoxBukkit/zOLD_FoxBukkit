package de.doridian.yiffbukkit.login;

import java.net.SocketAddress;

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

		String username = DoriLogin.verifyLogin(ip);
		if (username == null) {
			loginHandler.a("Failed to verify username!");
		}
		else {
			loginHandler.g = username;
			NetLoginHandler.a(loginHandler, loginPacket);
		}
	}

}
