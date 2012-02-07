package de.doridian.yiffbukkit.main.commands;

import de.doridian.yiffbukkitsplit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand.BooleanFlags;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.NumericFlags;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.ArrayList;
import java.util.List;

@Names("autoexec")
@Help("Schedules commands to be executed every time you connect.")
@Usage("[<command>|-r <index>]")
@Permission("yiffbukkitsplit.autoexec")
@BooleanFlags("e")
@NumericFlags("r")
public class AutoexecCommand extends ICommand implements Listener {
	public AutoexecCommand() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				execCommands(player);
			}
		});
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

			playerHelper.sendDirectedMessage(ply, "Removed command "+id+": \u00a79"+removedCommand+"\u00a7f.");
			listAutoexec(ply);

			return;
		}

		if (argStr.charAt(0) != '/')
			argStr = '/' + argStr;

		final List<String> commands = getAutoexec(ply);
		commands.add(argStr);
		playerHelper.saveAutoexecs();

		playerHelper.sendDirectedMessage(ply, "Added command "+(commands.size()-1)+": \u00a79"+argStr+"\u00a7f.");
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
			playerHelper.sendDirectedMessage(player, i+": \u00a79"+command);
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
