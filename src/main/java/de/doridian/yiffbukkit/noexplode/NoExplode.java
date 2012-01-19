package de.doridian.yiffbukkit.noexplode;

import de.doridian.yiffbukkit.YiffBukkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

public class NoExplode {
	final YiffBukkit yiffBukkit;
	public NoExplode(YiffBukkit yiffBukkit) {
		this.yiffBukkit = yiffBukkit;

		new NoExplodeEntityListener(this);

		reload();
	}

	public void reload() {
		File file = new File(yiffBukkit.getDataFolder()+"/NoExplode.properties");

		if (file.exists()) {
			Properties pr = new Properties();
			try {
				FileInputStream in = new FileInputStream(file);
				pr.load(in);
				explodetnt = Boolean.parseBoolean(pr.getProperty("explode-tnt"));
				explodecreeper = Boolean.parseBoolean(pr.getProperty("explode-creeper"));
				explodeghast = Boolean.parseBoolean(pr.getProperty("explode-ghast"));
				damagecreeper = Boolean.parseBoolean(pr.getProperty("damage-creeper"));
			}
			catch (IOException e) { }
		}
		else {
			File dir = new File(yiffBukkit.getDataFolder().toString());

			if (!dir.exists()) {
				dir.mkdir();
			}

			try {
				file.createNewFile();

				try {
					PrintWriter out = new PrintWriter(new FileWriter(yiffBukkit.getDataFolder()+"/NoExplode.properties"));

					out.println("explode-tnt=false");
					out.println("explode-creeper=false");
					out.println("explode-ghast=true");
					out.println("damage-creeper=true");
					out.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			catch(IOException e) { }
		}
	}

	public boolean explodetnt = false;
	public boolean explodecreeper = false;
	public boolean explodeghast = true;
	public boolean damagecreeper = true;
}