package de.doridian.yiffbukkit.config;

import de.doridian.yiffbukkit.YiffBukkit;

import java.io.FileWriter;
import java.io.IOException;

public class ConfigFileWriter extends FileWriter {
	public ConfigFileWriter(String file) throws IOException {
		super(YiffBukkit.instance.getDataFolder() + "/" + file);
	}
}
