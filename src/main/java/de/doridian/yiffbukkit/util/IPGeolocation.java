package de.doridian.yiffbukkit.util;

import com.maxmind.geoip.LookupService;
import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.config.ConfigFileWriter;

import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

public class IPGeolocation {
	private static final String DOWNLOADURL = "http://geolite.maxmind.com/download/geoip/database/GeoLiteCountry/GeoIP.dat.gz";
	private static String DATABASE;
	private static LookupService lookupSvc = null;

	public static void downloadFile() {
		new Thread() {
			@Override
			public void run() {
				try {
					URL url = new URL(DOWNLOADURL);
					URLConnection conn = url.openConnection();
					System.setProperty("http.agent", "");
					conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
					conn.setConnectTimeout(10000);
					conn.setReadTimeout(20000);
					InputStreamReader reader = new InputStreamReader(new GZIPInputStream(conn.getInputStream()));
					FileWriter writer = new FileWriter(DATABASE);
					char[] buff = new char[256]; int data;
					while((data = reader.read(buff)) > 0) {
						writer.write(buff, 0, data);
					}
					writer.close();
					reader.close();
				} catch(Exception e) { e.printStackTrace(); return; }
				lookupSvc = null;
				initialize();
			}
		}.start();
	}

	public static void initialize() {
		if(lookupSvc == null) {
			if(DATABASE == null) {
				downloadFile();
				DATABASE = YiffBukkit.instance.getDataFolder() + "/GeoIP.dat";
			}
			try {
				lookupSvc = new LookupService(DATABASE, LookupService.GEOIP_MEMORY_CACHE);
			} catch(Exception e) { }
		}
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