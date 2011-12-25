package de.doridian.yiffbukkit.permissions;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class YiffBukkitPermissibleBase implements Permissible {
	private Permissible parent = this;
	private CommandSender parentC = null;
	private PermissionHandler handler;
	private ServerOperator opable = null;
	
	private void __init() {
		handler = Permissions.Security;
	}
	private void __init_end() {
		if(this.parent == null) return;
		
		if(this.parent instanceof CommandSender) {
			this.parentC = (CommandSender)parent;
		}
		
		recalculatePermissions();
	}
	
	public YiffBukkitPermissibleBase(Permissible parent) {
		__init();
		
		this.parent = parent;
		
		__init_end();
	}
	
	public YiffBukkitPermissibleBase(ServerOperator opable) {
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
		return null;
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
		__addAllSubscribe(Server.BROADCAST_CHANNEL_USERS);
	}
	
	private void __addAllSubscribe(String perm) {
		__addSubscribe(this, perm);
		__addSubscribe(this.parent, perm);
	}
	
	private void __addSubscribe(Permissible what, String perm) {
		if(what == null) return;
		Bukkit.getServer().getPluginManager().subscribeToPermission(perm, what);
	}
}
