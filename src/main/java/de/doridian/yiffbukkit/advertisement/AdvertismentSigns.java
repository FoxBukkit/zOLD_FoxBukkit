package de.doridian.yiffbukkit.advertisement;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.config.ConfigFileReader;
import org.bukkit.Location;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.logging.Level;

public class AdvertismentSigns {
	private YiffBukkit plugin;
	
	private ArrayList<Timer> timers = new ArrayList<Timer>();
	private int[] letterwidth = new int[256];
	
	public AdvertismentSigns(YiffBukkit plug) {
		plugin = plug;
		
		BufferedReader stream;
		letterwidth[0] = 0;
		for(int i=1;i<256;i++) {
			try {
				stream = new BufferedReader(new ConfigFileReader("advertisments/letters/" + String.valueOf((char)i) + ".txt"));
				letterwidth[i] = stream.readLine().length();
				stream.close();
			}
			catch(Exception e) {
				letterwidth[i] = 0;
			}
		}
		
		ReloadAds();
	}
	
	public void ReloadAds() {
		int imax = timers.size();
		for(int i=0;i<imax;i++) {
			try {
				timers.get(i).cancel();
			}
			catch(Exception e) { e.printStackTrace(); }
		}
		timers.clear();
		BufferedReader stream = null;
		try {
			stream = new BufferedReader(new ConfigFileReader("advertisments/list.txt"));
			String line; BufferedReader innerStream; Timer timer; String[] lsplit; Location origin; AdvertismentSignTask newtask; byte[][] fileData = null;  
			
			while((line = stream.readLine()) != null) {
				lsplit = line.split(";", 5);
				
				innerStream = new BufferedReader(new ConfigFileReader("advertisments/" + lsplit[0] + ".txt"));
				
				int fullx = 0;
				int fully = Integer.valueOf(lsplit[3]);
				
				line = innerStream.readLine().toLowerCase();
				
				if(line.equals("pixels")) {
					innerStream.mark(256);
					fullx = innerStream.readLine().length();
					innerStream.reset();

					fileData = new byte[fullx][fully];
					
					parseFileIntoArray(0, fileData, innerStream, 'p');
					 
				}
				else if(line.equals("text")) {
					char color = 'p';
					
					line = innerStream.readLine().toLowerCase() + "  ";
					fully = 5;
					int jmax = line.length(); int j;
					for(j=0;j<jmax;j++) {
						fullx += 1 + letterwidth[line.charAt(j)];
					}
					fileData = new byte[fullx][fully];
					
					int x = 0;
					for(j=0;j<jmax;j++) {
						x += 1 + parseFileIntoArray(x, fileData, "letters/" + line.charAt(j), color);
					}
				}
				innerStream.close();
				
				origin = plugin.utils.unserializeLocation(lsplit[4]);
				newtask = new AdvertismentSignTask(origin, Byte.valueOf(lsplit[1]), Integer.valueOf(lsplit[2]), fully, fileData);
				timer = new Timer();
				timer.schedule(newtask, 200, 200);
				timers.add(timer);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		try {
			stream.close();
		}
		catch(Exception e) {
			
		}
	}
	
	private int parseFileIntoArray(int startx, byte[][] fileData, String file, char defcolor) {
		try {
			BufferedReader stream = new BufferedReader(new ConfigFileReader("advertisments/" + file + ".txt"));
			int ret = parseFileIntoArray(startx, fileData, stream, defcolor);
			stream.close();
			return ret;
		}
		catch(Exception e) {
			plugin.log(Level.WARNING, "Could not load advertisment file: " + file);
			e.printStackTrace();
			return 0;
		}
	}
	private int parseFileIntoArray(int startx, byte[][] fileData, BufferedReader stream, char defcolor) throws IOException {
		int y = 0;
		String line;
		int fullx = 0;
		char c;
		while((line = stream.readLine()) != null) {
			line = line.toLowerCase();
			if(fullx == 0) fullx = line.length();
			for(int x=0;x<fullx;x++) {
				c = line.charAt(x);
				if(c == ' ') c = 'a';
				else if(c == 'x') c = defcolor;
				fileData[x + startx][y] = (byte)(c - 'a');
			}
			y++;
		}
		return fullx;
	}
}
