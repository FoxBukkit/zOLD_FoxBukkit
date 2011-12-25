package de.doridian.yiffbukkit.advertisement;

import org.bukkit.Location;

import java.util.TimerTask;

public class AdvertismentSignTask extends TimerTask {
	private Location origin;
	private int sizex;
	private int sizey;
	private byte orientation;
	
	private int lengthx;
	private int lengthxd;
	
	private byte[][] currentDisplay;
	private byte[][] advertismentData;
	
	private int scrollerPos;
	
	public AdvertismentSignTask(Location originx, byte orientationx, int sizexx, int sizeyx, byte[][] advertismentDatax) {
		origin = originx.clone();
		orientation = orientationx;
		sizex = sizexx;
		sizey = sizeyx;
		currentDisplay = new byte[sizex][sizey];
		advertismentData = advertismentDatax.clone();
		scrollerPos = 0;
		lengthx = advertismentData.length;
		lengthxd = lengthx * 2;
	}
	
	public synchronized void run() {
		Location current = origin.clone();
		for(int y=0;y<sizey;y++) {
			for(int x=0;x<sizex;x++) {
				currentDisplay[x][y] = advertismentData[(x + scrollerPos) % lengthx][y];
				
				switch(orientation) {
					case 1:
						current.setX(origin.getX() + x);
						current.setY(origin.getY() - y);
						break;
					case 2:
						current.setZ(origin.getZ() + x);
						current.setY(origin.getY() - y);
						break;
					case 3:
						current.setX(origin.getX() - x);
						current.setY(origin.getY() - y);
						break;
					case 4:
						current.setZ(origin.getZ() - x);
						current.setY(origin.getY() - y);
						break;
				}
				
				current.getBlock().setData(currentDisplay[x][y]);
			}
		}
		
		scrollerPos = (scrollerPos + 1) % lengthxd;
	}
}
