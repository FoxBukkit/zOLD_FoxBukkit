package de.doridian.yiffbukkit.main.commands;

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
import de.doridian.yiffbukkitsplit.util.MessageHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

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
				final String toolName = entry.getKey();

				MessageHelper.sendMessage(ply, "<color name=\"yellow\">%1$s</color> => <color name=\"blue\">%2$s</color>", toolName, toolBind.name);
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

		final ToolBind toolBind = new ToolBind(parsedCommands.getCleanString(), ply) {
			@Override
			public boolean run(PlayerInteractEvent event) {
				parsedCommands.run(event.getPlayer());

				return true;
			}
		};

		ToolBind.add(ply, toolType, left, toolBind);

		MessageHelper.sendMessage(ply, "Bound <color name=\"blue\">%1$s</color> to your tool (<color name=\"yellow\">%2$s</color>). Right-click to use.", parsedCommands.getCleanString(), toolType.name());
	}

	public static void unbind(Player ply, Material toolType, boolean left) {
		if (ToolBind.remove(ply, toolType, left)) {
			MessageHelper.sendMessage(ply, "Unbound your tool (<color name=\"yellow\">%1$s</color>).", toolType.name());
		}
		else {
			MessageHelper.sendMessage(ply, "Your tool (<color name=\"yellow\">%1$s</color>) was not bound.", toolType.name());
		}
	}
}
