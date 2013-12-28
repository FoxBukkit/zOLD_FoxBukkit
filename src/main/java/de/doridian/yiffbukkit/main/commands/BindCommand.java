package de.doridian.yiffbukkit.main.commands;

import de.doridian.yiffbukkit.core.util.MessageHelper;
import de.doridian.yiffbukkit.main.ToolBind;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.BooleanFlags;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.StringFlags;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.main.util.RunString;
import de.doridian.yiffbukkit.spawning.commands.GiveCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Names("bind")
@Help(
		"Binds a command to your current tool. The leading slash\n" +
		"is optional. Unbind by typing '/bind' without arguments.\n" +
		"Flags:\n" +
		"  -l lists your current binds.\n" +
		"  -i <item name or id> together with -e to bind to a specific tool\n" +
		"  -x to bind to the left instead of the right mouse button"
)
@Usage("-l|[-i <item name or id>][<command>[;<command>[;<command> ...]]]")
@Permission("yiffbukkit.bind")
@BooleanFlags("lx")
@StringFlags("i")
public class BindCommand extends ICommand {
	private static final Set<String> filter = new HashSet<>();

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
			final String playerName = ply.getName();
			for (Entry<String, ToolBind> entry : ToolBind.list(playerName).entrySet()) {
				final ToolBind toolBind = entry.getValue();
				final String toolKey = entry.getKey();
				final Pattern pattern = Pattern.compile("^(.*)(true|false)$");
				final Matcher matcher = pattern.matcher(toolKey);
				if (!matcher.matches()) {
					throw new YiffBukkitCommandException("Error in one of the tool bind keys!");
				}
				final String toolName = matcher.group(1);
				final boolean left = Boolean.parseBoolean(matcher.group(2));

				MessageHelper.sendMessage(ply, String.format("<color name=\"yellow\">%1$s</color> => <color name=\"blue\">%2$s</color> " + getButtonsForBind(toolBind, toolName, left), toolName, toolBind.name));
			}
			return;
		}

		final Material toolType;
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

		final boolean left = booleanFlags.contains('x');

		if (argStr.isEmpty()) {
			unbind(ply, toolType, left);
			return;
		}

		final RunString parsedCommands = new RunString(argStr, filter);

		final ToolBind toolBind = new ToolBind(getColoredRunString(parsedCommands), ply) {
			@Override
			public boolean run(PlayerInteractEvent event) {
				parsedCommands.run(event.getPlayer());

				return true;
			}

			@Override
			public String getRestoreCommand(String toolName, boolean left) {
				return String.format("/bind -i %s %s", toolName, parsedCommands.getString());
			}
		};

		ToolBind.add(ply, toolType, left, toolBind);

		MessageHelper.sendMessage(ply, String.format("Bound <color name=\"blue\">%1$s</color> to your tool (<color name=\"yellow\">%2$s</color>). Right-click to use. " + getButtonsForBind(toolBind, toolType.name(), left), getColoredRunString(parsedCommands), toolType.name()));
	}

	private String getButtonsForBind(ToolBind toolBind, String toolName, boolean left) {
		return getRemoveButton(toolName, left) + " " + getAutoexecButton(toolBind, toolName, left);
	}

	private String getColoredRunString(RunString parsedCommands) {
		return parsedCommands.getString("<color name=\"red\">;</color> ");
	}

	public static void unbind(Player ply, Material toolType, boolean left) {
		final ToolBind removedToolBind = ToolBind.remove(ply, toolType, left);
		if (removedToolBind == null) {
			MessageHelper.sendMessage(ply, "Your tool (<color name=\"yellow\">%1$s</color>) was not bound.", toolType.name());
		} else {
			MessageHelper.sendMessage(ply, "Unbound your tool (<color name=\"yellow\">%1$s</color>). " + getRestoreButton(removedToolBind, toolType.name(), left), toolType.name());
		}
	}

	private static String getRemoveButton(String toolName, boolean left) {
		final String format;
		if (left)
			format = "/bind -x -i %s";
		else
			format = "/bind -i %s";

		return MessageHelper.button(String.format(format, toolName), "x", "red", true);
	}

	private static String getAutoexecButton(ToolBind toolBind, String toolName, boolean left) {
		final String restoreCommand = toolBind.getRestoreCommand(toolName, left);
		if (restoreCommand == null)
			return "";

		return MessageHelper.button(String.format("/autoexec " + restoreCommand), "auto", "blue", true);
	}

	private static String getRestoreButton(ToolBind toolBind, String toolName, boolean left) {
		final String restoreCommand = toolBind.getRestoreCommand(toolName, left);
		if (restoreCommand == null)
			return "";

		return MessageHelper.button(restoreCommand, "restore", "dark_green", false);
	}
}
