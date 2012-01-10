package de.doridian.yiffbukkit.util;

import com.maxmind.geoip.LookupService;

import java.net.InetSocketAddress;

public class IPGeolocation {
	public static final String DATABASE = "GeoIP.dat";

	private static LookupService lookupSvc = null;
	static {
		try {
			lookupSvc = new LookupService(DATABASE, LookupService.GEOIP_MEMORY_CACHE);
		} catch(Exception e) { }
	}

	public static String getCountry(String ip) {
		try {
			return lookupSvc.getCountry(ip).getName();
		} catch(Exception e) {
			return "N/A";
		}
	}

	public static String getCountry(InetSocketAddress ip) {
		return getCountry(ip.getAddress().getHostAddress());
	}
}
