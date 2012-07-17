package de.doridian.yiffbukkit.main.commands;

import de.doridian.yiffbukkit.main.ToolBind;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.spawning.commands.GiveCommand;
import de.doridian.yiffbukkit.main.commands.ICommand.BooleanFlags;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.StringFlags;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Names("bind")
@Help(
		"Binds a command to your current tool. The leading slash\n" +
		"is optional. Unbind by typing '/bind' without arguments.\n" +
		"The -l flag lists your current binds."
)
@Usage("-l|[-i <item name or id>][<command>[;<command>[;<command> ...]]]")
@Permission("yiffbukkit.bind")
@BooleanFlags("l")
@StringFlags("i")
public class BindCommand extends ICommand {
	private static final Set<String> filter = new HashSet<String>();

	static {
		filter.add("/pm");
		filter.add("/msg");
		filter.add("/emote");
		filter.add("/say");
		filter.add("/me");
		filter.add("/emote");
		filter.add("/throw");
		filter.add("/bind");
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		argStr = parseFlags(argStr).trim();

		if (booleanFlags.contains('l')) {
			String playerName = ply.getName();
			for (Entry<String, ToolBind> entry : ToolBind.list(playerName).entrySet()) {
				ToolBind toolBind = entry.getValue();
				String toolName = entry.getKey();

				PlayerHelper.sendDirectedMessage(ply, "\u00a7e"+toolName+"\u00a7f => \u00a79"+toolBind.name);
			}
		}

		Material toolType;
		if (stringFlags.containsKey('i')) {
			final String materialName = stringFlags.get('i');

			toolType = GiveCommand.matchMaterial(materialName);
			if (toolType == null)
				throw new YiffBukkitCommandException("Material "+materialName+" not found");
		}
		else if (!argStr.isEmpty() && argStr.charAt(0) == '-') {
			throw new YiffBukkitCommandException("Invalid flag specified");
		}
		else {
			toolType = ply.getItemInHand().getType();
		}

		if (argStr.isEmpty()) {
			ToolBind.remove(ply, toolType);

			PlayerHelper.sendDirectedMessage(ply, "Unbound your tool (\u00a7e"+toolType.name()+"\u00a7f).");

			return;
		}

		final Pattern commandPattern = Pattern.compile("^([^ ]+).*$");
		final List<String> commands = new ArrayList<String>();
		boolean first = true;
		StringBuilder sb = new StringBuilder();
		for (String command : argStr.split(";")) {
			command = command.trim();
			if (command.charAt(0) != '/')
				command = '/' + command;

			Matcher commandMatcher = commandPattern.matcher(command);

			if (!commandMatcher.matches())
				continue;

			if (filter.contains(commandMatcher.group(1)))
				throw new YiffBukkitCommandException("Command \u00a79"+commandMatcher.group(1)+"\u00a7f cannot be bound.");

			commands.add(command);

			if (!first)
				sb.append("\u00a7c; \u00a79");
			first = false;

			sb.append(command);
		}
		final String commandString = sb.toString();

		ToolBind toolBind = new ToolBind(commandString, ply) {
			@Override
			public void run(PlayerInteractEvent event) {
				Player player = event.getPlayer();

				for (String command : commands) {
					player.chat(command);
				}
			}
		};

		ToolBind.add(ply, toolType, toolBind);

		PlayerHelper.sendDirectedMessage(ply, "Bound \u00a79"+commandString+"\u00a7f to your tool (\u00a7e"+toolType.name()+"\u00a7f). Right-click to use.");
	}
}
