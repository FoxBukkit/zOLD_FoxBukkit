package de.doridian.yiffbukkit.main.commands;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;

import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkitsplit.YiffBukkit;

public class CommandSystem {
	private final YiffBukkit plugin;
	private final Map<String,ICommand> commands = new HashMap<String,ICommand>();

	public CommandSystem(YiffBukkit plugin) {
		this.plugin = plugin;
		plugin.commandSystem = this;
		scanCommands();
	}

	public void scanCommands() {
		commands.clear();
		scanCommands("de.doridian.yiffbukkit.advanced.commands");
		scanCommands("de.doridian.yiffbukkit.chat.commands");
		scanCommands("de.doridian.yiffbukkit.fun.commands");
		scanCommands("de.doridian.yiffbukkit.irc.commands");
		scanCommands("de.doridian.yiffbukkit.main.commands");
		scanCommands("de.doridian.yiffbukkit.mcbans.commands");
		scanCommands("de.doridian.yiffbukkit.permissions.commands");
		scanCommands("de.doridian.yiffbukkit.remote.commands");
		scanCommands("de.doridian.yiffbukkit.spawning.commands");
		//scanCommands("de.doridian.yiffbukkit.ssl.commands");
		scanCommands("de.doridian.yiffbukkit.teleportation.commands");
		scanCommands("de.doridian.yiffbukkit.transmute.commands");
		scanCommands("de.doridian.yiffbukkit.warp.commands");
	}

	public void scanCommands(String packageName) {
		for (Class<? extends ICommand> commandClass : getSubClasses(ICommand.class, packageName)) {
			try {
				commandClass.newInstance();
			}
			catch (InstantiationException e) {
				// We try to instantiate an interface
				// or an object that does not have a 
				// default constructor
				continue;
			}
			catch (IllegalAccessException e) {
				// The class/ctor is not public
				continue;
			}
			catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	private static <T> List<Class<? extends T>> getSubClasses(Class<T> baseClass, String packageName) {
		final List<Class<? extends T>> ret = new ArrayList<Class<? extends T>>();
		final File file;
		try {
			final ProtectionDomain protectionDomain = baseClass.getProtectionDomain();
			final CodeSource codeSource = protectionDomain.getCodeSource();
			final URL location = codeSource.getLocation();
			final URI uri = location.toURI();
			file = new File(uri);
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
			return ret;
		}
		final String[] fileList;

		if (file.isDirectory() || (file.isFile() && !file.getName().endsWith(".jar"))) {
			String packageFolderName = "/"+packageName.replace('.','/');

			URL url = baseClass.getResource(packageFolderName);
			if (url == null)
				return ret;

			File directory = new File(url.getFile());
			if (!directory.exists())
				return ret;

			// Get the list of the files contained in the package
			fileList = directory.list();
		}
		else if (file.isFile()) {
			final List<String> tmp = new ArrayList<String>();
			final JarFile jarFile;
			try {
				jarFile = new JarFile(file);
			}
			catch (IOException e) {
				e.printStackTrace();
				return ret;
			}

			Pattern pathPattern = Pattern.compile(packageName.replace('.','/')+"/(.+\\.class)");
			final Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				Matcher matcher = pathPattern.matcher(entries.nextElement().getName());
				if (!matcher.matches())
					continue;

				tmp.add(matcher.group(1));
			}

			fileList = tmp.toArray(new String[tmp.size()]);
		}
		else {
			return ret;
		}

		Pattern classFilePattern = Pattern.compile("(.+)\\.class");
		for (String fileName : fileList) {
			// we are only interested in .class files
			Matcher matcher = classFilePattern.matcher(fileName);
			if (!matcher.matches())
				continue;

			// removes the .class extension
			String classname = matcher.group(1);
			try {
				final String qualifiedName = packageName+"."+classname.replace('/', '.');
				final Class<?> classObject = Class.forName(qualifiedName);
				final Class<? extends T> classT = classObject.asSubclass(baseClass);

				// Try to create an instance of the object
				ret.add(classT);
			}
			catch (ClassCastException e) {
				continue;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	public void registerCommand(String name, ICommand command) {
		commands.put(name, command);
	}

	public Map<String,ICommand> getCommands() {
		return commands;
	}

	public boolean runCommand(CommandSender commandSender, String cmd, String argStr) {
		String args[];
		if(argStr.isEmpty()) {
			args = new String[0];
		} else {
			args = argStr.split(" +");
		}
		return runCommand(commandSender, cmd, args, argStr);
	}

	public boolean runCommand(CommandSender commandSender, String cmd, String[] args, String argStr) {
		if (commands.containsKey(cmd)) {
			ICommand icmd = commands.get(cmd);
			try {
				if(!icmd.canPlayerUseCommand(commandSender)) {
					throw new PermissionDeniedException();
				}
				if(!(cmd.equals("msg") || cmd.equals("pm") || cmd.equals("conv") || cmd.equals("conversation")))
				{
					String logmsg = "YB Command: " + commandSender.getName() + ": "  + cmd + " " + argStr;
					plugin.ircbot.sendToStaffChannel(logmsg);
					plugin.log(logmsg);
				}
				icmd.run(commandSender, args, argStr);
			}
			catch (YiffBukkitCommandException e) {
				plugin.playerHelper.sendDirectedMessage(commandSender,e.getMessage(), e.getColor());
			}
			catch (Exception e) {
				if (commandSender.hasPermission("yiffbukkit.detailederrors")) {
					plugin.playerHelper.sendDirectedMessage(commandSender,"Command error: "+e+" in "+e.getStackTrace()[0]);
					e.printStackTrace();
				}
				else {
					plugin.playerHelper.sendDirectedMessage(commandSender,"Command error!");
				}
			}
			return true;
		}
		return false;
	}

	public boolean runCommand(CommandSender commandSender, String baseCmd) {
		int posSpace = baseCmd.indexOf(' ');
		String cmd; String argStr;
		if (posSpace < 0) {
			cmd = baseCmd.toLowerCase();
			argStr = "";
		} else {
			cmd = baseCmd.substring(0, posSpace).trim().toLowerCase();
			argStr = baseCmd.substring(posSpace).trim();
		}
		return runCommand(commandSender, cmd, argStr);
	}
}
