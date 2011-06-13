package de.doridian.yiffbukkit.mcbans;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class MCBansUtil {
	private final static String APIKEY = "SECRET";
	private static JSONParser parser = new JSONParser();
	
	public static JSONObject apiQuery(String data) {
		try {
			URL url = new URL("http://api.mcbans.com/" + APIKEY);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
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
