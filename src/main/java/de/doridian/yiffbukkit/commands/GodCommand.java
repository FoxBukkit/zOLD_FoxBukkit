package de.doridian.yiffbukkit.commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.plugin.PluginManager;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.YiffBukkitCommandException;

public class GodCommand extends ICommand {
	Set<String> godded = new HashSet<String>();

	public GodCommand(YiffBukkit plug) {
		super(plug);
		EntityListener entityListener = new EntityListener() {
			@Override
			public void onEntityDamage(EntityDamageEvent event) {
				if (!(event.getEntity() instanceof Player))
					return;

				Player ply = (Player)event.getEntity();

				String playerName = ply.getName();

				if (godded.contains(playerName))
					event.setCancelled(true);
			}
		};

		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Highest, plugin);
	}

	@Override
	public int GetMinLevel() {
		return 4;
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		Boolean onoff;
		String name;
		switch (args.length){
		case 0:
			//god - toggle own god mode
			onoff = null;
			name = ply.getName();

			break;

		case 1:
			if ("on".equals(args[0])) {
				//god on - turn own god mode on
				onoff = true;
				name = ply.getName();
			}
			else if ("off".equals(args[0])) {
				//god off - turn own god mode off
				onoff = false;
				name = ply.getName();
			}
			else {
				//god <name> - toggle someone's god mode
				onoff = null;
				name = playerHelper.CompletePlayerName(args[0], false);
			}
			break;

		default:
			if ("on".equals(args[0])) {
				//god on <name> - turn someone's god mode on
				onoff = true;
				name = playerHelper.CompletePlayerName(args[1], false);
			}
			else if ("off".equals(args[0])) {
				//god off <name> - turn someone's god mode off
				onoff = false;
				name = playerHelper.CompletePlayerName(args[1], false);
			}
			else {
				//god <name> <...> - not sure yet
				name = playerHelper.CompletePlayerName(args[0], false);

				if ("on".equals(args[1])) {
					//god <name> on - turn someone's god mode on
					onoff = true;
				}
				else if ("off".equals(args[1])) {
					//god <name> off - turn someone's god mode off
					onoff = false;
				}
				else {
					throw new YiffBukkitCommandException("Syntax error");
				}
			}
			break;
		}

		if (onoff == null) {
			onoff = !godded.contains(name);
		}

		if (onoff) {
			godded.add(name);
			playerHelper.SendServerMessage(ply.getName() + " made " + name + " invincible.");
		}
		else {
			godded.remove(name);
			playerHelper.SendServerMessage(ply.getName() + " made " + name + " no longer invincible.");
		}
	}

	@Override
	public String GetHelp() {
		return "Activates or deactivates god mode.";
	}

	@Override
	public String GetUsage() {
		return "[<name>] [on|off]";
	}
}
