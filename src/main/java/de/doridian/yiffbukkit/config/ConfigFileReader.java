package de.doridian.yiffbukkit.config;

import de.doridian.yiffbukkit.YiffBukkit;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ConfigFileReader extends FileReader {
	public ConfigFileReader(String file) throws FileNotFoundException {
		super(YiffBukkit.instance.getDataFolder() + "/" + file);
	}
}
