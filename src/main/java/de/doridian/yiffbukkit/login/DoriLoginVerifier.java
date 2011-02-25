package de.doridian.yiffbukkit.login;

import net.minecraft.server.ILoginVerifier;
import net.minecraft.server.NetLoginHandler;
import net.minecraft.server.Packet1Login;

public class DoriLoginVerifier implements ILoginVerifier {

	@Override
	public void verify(NetLoginHandler loginHandler, Packet1Login loginPacket) {
		(new ThreadDoriLoginVerifier(loginHandler, loginPacket)).start();
	}

}
