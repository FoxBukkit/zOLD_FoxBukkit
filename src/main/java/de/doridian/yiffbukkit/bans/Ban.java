package de.doridian.yiffbukkit.bans;

import java.util.UUID;

public class Ban {
	private String reason;
	private int admin;
	private int user;
	private String type;
	private int time;

	protected final long retrievalTime;

	protected Ban(String reason, int admin, int user, String type, int time) {
		this.reason = reason;
		this.admin = admin;
		this.user = user;
		this.type = type;
		this.time = time;
		this.retrievalTime = System.currentTimeMillis();
	}

	public Ban() {
		this.retrievalTime = System.currentTimeMillis();
		refreshTime();
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public int getAdminID() {
		return admin;
	}

	public int getUserID() {
		return user;
	}

	public BanPlayer getAdmin() {
		return BanResolver.getUserByID(admin);
	}

	public BanPlayer getUser() {
		return BanResolver.getUserByID(user);
	}

	public void setUser(String username, UUID uuid) {
		this.user = BanResolver.getUserID(username, uuid, true);
	}

	public void setAdmin(String adminname, UUID uuid) {
		this.admin = BanResolver.getUserID(adminname, uuid, true);
	}

	public void setUser(int user) {
		this.user = user;
	}

	public void setAdmin(int admin) {
		this.admin = admin;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void refreshTime() { this.time = (int)(System.currentTimeMillis() / 1000); }

	public int getTime() {
		return time;
	}
}
