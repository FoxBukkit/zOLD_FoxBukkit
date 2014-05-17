package de.doridian.yiffbukkit.main.config;

import de.doridian.yiffbukkit.core.YiffBukkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ConfigFileReader extends FileReader {
	public ConfigFileReader(String file) throws FileNotFoundException {
		super(new File(YiffBukkit.instance.getDataFolder(), file));
	}
}
