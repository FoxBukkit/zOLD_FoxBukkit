package de.doridian.yiffbukkit;

public class PermissionDeniedException extends YiffBukkitCommandException {
	private static final long serialVersionUID = 1L;

	public PermissionDeniedException() {
		super("Permission denied!");
	}

	public PermissionDeniedException(String message) {
		super(message);
	}

	public PermissionDeniedException(Throwable cause) {
		super("Permission denied!", cause);
	}

	public PermissionDeniedException(String message, Throwable cause) {
		super(message, cause);
	}

}
