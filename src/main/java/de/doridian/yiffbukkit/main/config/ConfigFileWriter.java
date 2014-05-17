package de.doridian.yiffbukkit.main.config;

import de.doridian.yiffbukkit.core.YiffBukkit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigFileWriter extends FileWriter {
	public ConfigFileWriter(String file) throws IOException {
		super(new File(YiffBukkit.instance.getDataFolder(), file));
	}
}
