package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.util.PlayerHelper;

public abstract class ICommand {
	public @interface Level { int value(); }
	protected @interface Names { String[] value(); }
	protected @interface Help { String value(); }
	protected @interface Usage { String value(); }
	protected @interface Disabled { }

	protected YiffBukkit plugin;
	protected PlayerHelper playerHelper;

	protected ICommand(YiffBukkit plug) {
		plugin = plug;
		playerHelper = plugin.playerHelper;

		if (this.getClass().getAnnotation(Disabled.class) != null)
			return;
		
		Names namesAnnotation = this.getClass().getAnnotation(Names.class);
		if (namesAnnotation == null)
			return;

		for (String name : namesAnnotation.value()) {
			plugin.playerListener.commands.put(name, this);
		}
	}

	public int GetMinLevel() {
		Level levelAnnotation = this.getClass().getAnnotation(Level.class);
		if (levelAnnotation == null)
			throw new UnsupportedOperationException("You need either a GetMinLevel method or an @Level annotation.");
		
		return levelAnnotation.value();
	}
	public abstract void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException;
	public String GetHelp() {
		Help helpAnnotation = this.getClass().getAnnotation(Help.class);
		if (helpAnnotation == null)
			return "";

		return helpAnnotation.value();
	}
	public String GetUsage() {
		Usage usageAnnotation = this.getClass().getAnnotation(Usage.class);
		if (usageAnnotation == null)
			return "";

		return usageAnnotation.value();
	}

	public boolean CanPlayerUseCommand(Player ply)
	{
		int plylvl = plugin.playerHelper.GetPlayerLevel(ply);
		int reqlvl = GetMinLevel();

		return (plylvl >= reqlvl);
	}
}
