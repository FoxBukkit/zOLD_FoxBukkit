package de.doridian.yiffbukkit.permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class YiffBukkitPermissibleBase extends PermissibleBase {
	private Permissible parent = this;
	private CommandSender parentC = null;
	private ServerOperator opable = null;

	private YiffBukkitPermissionHandler handler;
	private void __init() {
		handler = YiffBukkitPermissionHandler.instance;
	}
	private void __init_end() {
		if(this.parent == null) return;

		if(this.parent instanceof CommandSender) {
			this.parentC = (CommandSender)parent;
		}
		
		recalculatePermissions();
	}
	
	public YiffBukkitPermissibleBase(Permissible parent) {
		super(parent);

		__init();
		
		this.parent = parent;
		
		__init_end();
	}
	
	public YiffBukkitPermissibleBase(ServerOperator opable) {
		super(opable);

		__init();
		
        this.opable = opable;

		if(opable instanceof Permissible) {
			this.parent = (Permissible)opable;
		}
		
		__init_end();
    }

	@Override
	public boolean isOp() {
		if(opable != null)
			return opable.isOp();
		else if(parent != null)
			return parent.isOp();
		else
			return false;
	}

	@Override
	public void setOp(boolean arg0) {
		if(opable != null)
			opable.setOp(arg0);
		else if(parent != null)
			parent.setOp(arg0);
		
		recalculatePermissions();
	}

    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
    	recalculatePermissions();
    	return null;
    }

    public PermissionAttachment addAttachment(Plugin plugin) {
    	recalculatePermissions();
    	return null;
    }

    public void removeAttachment(PermissionAttachment attachment) {
    	recalculatePermissions();
    }
    
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
    	recalculatePermissions();
        return null;
    }

    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
    	recalculatePermissions();
    	return null;
    }

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return new HashSet<PermissionAttachmentInfo>();
	}

	@Override
	public boolean hasPermission(String arg0) {
		if(this.parentC instanceof Player) {
			return handler.has((Player)this.parentC, arg0.toLowerCase());
		} else if(this.parentC instanceof ConsoleCommandSender) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean hasPermission(Permission arg0) {
		return hasPermission(arg0.getName());
	}

	@Override
	public boolean isPermissionSet(String arg0) {
		return true;
	}

	@Override
	public boolean isPermissionSet(Permission arg0) {
		return true;
	}

	@Override
	public void recalculatePermissions() {

	}
}
