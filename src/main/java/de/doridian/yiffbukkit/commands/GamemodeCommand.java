package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Permission;
import de.doridian.yiffbukkit.commands.ICommand.Usage;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@Names("gamemode")
@Help("Sets the gamemode (creative / survival) for a player (default: you)")
@Usage("<gamemode> [player]")
@Permission("yiffbukkit.gamemode.self")
public class GamemodeCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		Player target = ply;
		if(args.length > 1)
			target = plugin.playerHelper.matchPlayerSingle(args[1]);
		
		if(target != ply && !plugin.permissionHandler.has(ply, "yiffbukkit.gamemode.others"))
			throw new PermissionDeniedException();
		
		GameMode targetMode = null;
		try {
			String arg = args[0].toLowerCase();
			char firstChar = arg.charAt(0);
			
			switch(firstChar) {
				case 'c':
					targetMode = GameMode.CREATIVE;
					break;
				case 's':
					targetMode = GameMode.SURVIVAL;
					break;
				default:
					switch(Integer.parseInt(arg)) {
						case 0:
							targetMode = GameMode.SURVIVAL;
							break;
						case 1:
							targetMode = GameMode.CREATIVE;
							break;
					}
			}
		}
		catch(Exception e) { }
		
		if(targetMode == null)
			throw new YiffBukkitCommandException("Invalid gamemode specified");
		
		target.setGameMode(targetMode);
		if(target == ply) {
			plugin.playerHelper.sendServerMessage(ply.getName() + " changed their gamemode to " + targetMode.toString());
		} else {
			plugin.playerHelper.sendServerMessage(ply.getName() + " changed the gamemode of " + target.getName() + " to " + targetMode.toString());
		}
	}
}
