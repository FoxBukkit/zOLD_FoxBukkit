package de.doridian.yiffbukkitsplit;

public enum LockDownMode {
	NONE("The server is already unlocked."),
	KICK("The server will already kick guests."),
	FIREWALL("The server will already firewall guests.");

	private final String description;

	private LockDownMode(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}