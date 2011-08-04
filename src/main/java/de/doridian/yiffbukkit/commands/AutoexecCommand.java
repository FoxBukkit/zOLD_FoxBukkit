package de.doridian.yiffbukkit.commands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("autoexec")
@Help("Schedules commands to be executed every time you connect.")
@Usage("[<command>|-r <index>]")
@Permission("yiffbukkit.autoexec")
@BooleanFlags("e")
@NumericFlags("r")
public class AutoexecCommand extends ICommand {
	public AutoexecCommand() {
		plugin.getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN, new PlayerListener() {
			@Override
			public void onPlayerJoin(PlayerJoinEvent event) {
				final Player player = event.getPlayer();

				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						execCommands(player);
					}
				});
			}
		}, Priority.Lowest, plugin);
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		if (argStr.trim().isEmpty()) {
			listAutoexec(ply);

			return;
		}

		argStr = parseFlags(argStr);

		if (booleanFlags.contains('e')) {
			execCommands(ply);

			return;
		}

		if (numericFlags.containsKey('r')) {
			final List<String> commands = playerHelper.autoexecs.get(ply.getName());

			final int id = (int) (double) numericFlags.get('r');

			if (commands == null)
				throw new YiffBukkitCommandException("You never defined an autoexec.");
			if (commands.size() <= id)
				throw new YiffBukkitCommandException("Index out of range.");

			String removedCommand = commands.remove(id);
			if (commands.isEmpty())
				playerHelper.autoexecs.remove(ply.getName());

			playerHelper.saveAutoexecs();

			playerHelper.sendDirectedMessage(ply, "Removed command "+id+": §9"+removedCommand+"§f.");
			listAutoexec(ply);

			return;
		}

		if (argStr.charAt(0) != '/')
			argStr = '/' + argStr;

		final List<String> commands = getAutoexec(ply);
		commands.add(argStr);
		playerHelper.saveAutoexecs();

		playerHelper.sendDirectedMessage(ply, "Added command "+(commands.size()-1)+": §9"+argStr+"§f.");
		listAutoexec(ply);
	}

	private List<String> getAutoexec(Player player) {
		List<String> commands = playerHelper.autoexecs.get(player.getName());
		if (commands == null)
			playerHelper.autoexecs.put(player.getName(), commands = new ArrayList<String>());
		return commands;
	}

	private void listAutoexec(Player player) {
		playerHelper.sendDirectedMessage(player, "Current autoexec:");

		List<String> commands = playerHelper.autoexecs.get(player.getName());
		if (commands == null || commands.isEmpty()) {
			playerHelper.sendDirectedMessage(player, "<empty>");
			return;
		}

		for (int i = 0; i < commands.size(); ++i) {
			String command = commands.get(i);
			playerHelper.sendDirectedMessage(player, i+": §9"+command);
		}
	}

	private void execCommands(final Player player) {
		List<String> commands = playerHelper.autoexecs.get(player.getName());
		if (commands == null)
			return;

		for (String command : commands) {
			player.chat(command);
		}
	}
}
