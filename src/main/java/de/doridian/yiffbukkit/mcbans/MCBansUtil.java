package de.doridian.yiffbukkit.mcbans;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import de.doridian.yiffbukkit.util.Configuration;

public class MCBansUtil {
	private final static String APIKEY = Configuration.getValue("mcbans-api-key", "");
	private static JSONParser parser = new JSONParser();
	
	public static boolean isKeyYesOrNo(JSONObject connret, String key) {
		if(connret.containsKey(key) && connret.get(key).toString().toLowerCase().charAt(0) == 'y') return true;
		return false;
	}
	
	public static JSONObject apiQuery(String data) {
		try {
			URL url = new URL("http://api.mcbans.com/v2/" + APIKEY);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(20000);
			conn.setDoOutput(true);
			
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write(data);
			writer.flush();
			writer.close();
			
			return (JSONObject)parser.parse(new InputStreamReader(conn.getInputStream()));
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static String URLEncode(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return URLEncoder.encode(str);
		}
	}
}
