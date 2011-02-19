package com.bukkit.doridian.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

class NullSocket extends Socket {
	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(new byte[32]);
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return new ByteArrayOutputStream();
	}

	@Override
	public SocketAddress getRemoteSocketAddress() {
		return new InetSocketAddress(0);
	}

	@Override
	public void setTrafficClass(int tc) throws SocketException {
	}
	
}
