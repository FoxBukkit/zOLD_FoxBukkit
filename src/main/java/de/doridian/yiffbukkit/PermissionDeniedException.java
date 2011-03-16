package de.doridian.yiffbukkit;

public class PermissionDeniedException extends YiffBukkitCommandException {
	private static final long serialVersionUID = 1L;

	public PermissionDeniedException() {
		this("Permission denied!");
	}

	public PermissionDeniedException(String message) {
		super(message);
		setColor('4');
	}

	public PermissionDeniedException(Throwable cause) {
		this("Permission denied!", cause);
	}

	public PermissionDeniedException(String message, Throwable cause) {
		super(message, cause);
		setColor('4');
	}

}
