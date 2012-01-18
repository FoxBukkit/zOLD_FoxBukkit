package de.doridian.yiffbukkit.noexplode;

import de.doridian.yiffbukkit.YiffBukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.logging.Logger;

public class NoExplode
{
	public YiffBukkit yiffbukkit;
	public NoExplode(YiffBukkit yiffbukkit) {
		this.yiffbukkit = yiffbukkit;

		entityListener = new NoExplodeEntityListener(this);

		reload();
	}

	public static final Logger log = Logger.getLogger("Minecraft");
	private final NoExplodeEntityListener entityListener;

	public void reload()
	{
		File file = new File(yiffbukkit.getDataFolder()+"/NoExplode.properties");

		if (file.exists())
		{
			Properties pr = new Properties();
			try
			{
				FileInputStream in = new FileInputStream(file);
				pr.load(in);
				explodetnt = Boolean.parseBoolean(pr.getProperty("explode-tnt"));
				explodecreeper = Boolean.parseBoolean(pr.getProperty("explode-creeper"));
				explodeghast = Boolean.parseBoolean(pr.getProperty("explode-ghast"));
				damagecreeper = Boolean.parseBoolean(pr.getProperty("damage-creeper"));
			}
			catch (IOException e)
			{
			}
		}
		else
		{
			File dir = new File(yiffbukkit.getDataFolder().toString());

			if (!dir.exists())
			{
				dir.mkdir();
			}

			try
			{
				file.createNewFile();

				try
				{
					PrintWriter out = new PrintWriter(new FileWriter(yiffbukkit.getDataFolder()+"/NoExplode.properties"));

					out.println("explode-tnt=false");
					out.println("explode-creeper=false");
					out.println("explode-ghast=true");
					out.println("damage-creeper=true");
					out.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			catch(IOException ioe)
			{
			}
		}
	}

	public boolean explodetnt = false;
	public boolean explodecreeper = false;
	public boolean explodeghast = true;
	public boolean damagecreeper = true;
}