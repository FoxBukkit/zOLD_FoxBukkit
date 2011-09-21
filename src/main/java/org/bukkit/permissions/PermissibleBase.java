package org.bukkit.permissions;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class PermissibleBase implements Permissible {
	private Permissible parent = this;
	private CommandSender parentC = null;
	private PermissionHandler handler;
	private ServerOperator opable = null;
	
	private void __init() {
		Permissions perm = ((Permissions)Bukkit.getServer().getPluginManager().getPlugin("Permissions"));
		handler = perm.getHandler();
	}
	private void __init_end() {
		if(this.parent == null) return;
		
		if(this.parent instanceof CommandSender) {
			this.parentC = (CommandSender)parent;
			Bukkit.getServer().getPluginManager().subscribeToPermission("bukkit.broadcast.user", this.parentC);
		}
	}
	
	public PermissibleBase(Permissible parent) {
		__init();
		
		this.parent = parent;
		
		__init_end();
	}
	
	public PermissibleBase(ServerOperator opable) {
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
	}

    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
    	return null;
    }

    public PermissionAttachment addAttachment(Plugin plugin) {
    	return null;
    }

    public void removeAttachment(PermissionAttachment attachment) {
        
    }
    
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return null;
    }

    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
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
		// TODO Auto-generated method stub

	}
}
