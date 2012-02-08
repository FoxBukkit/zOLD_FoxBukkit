package de.doridian.yiffbukkit.mcbans;

import de.doridian.yiffbukkit.main.util.Configuration;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class MCBansUtil {
	private final static String APIKEY = Configuration.getValue("mcbans-api-key", "");
	private static JSONParser parser = new JSONParser();
	
	public static boolean isKeyYesOrNo(JSONObject connret, String key) {
		if(connret.containsKey(key) && connret.get(key).toString().toLowerCase().charAt(0) == 'y') return true;
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject apiQuery(String data) {
		try {
			URL url = new URL("http://api.mcbans.com/v2/" + APIKEY);
			URLConnection conn = url.openConnection();
			System.setProperty("http.agent", "");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(20000);
			conn.setDoOutput(true);
			
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write(data);
			writer.flush();
			writer.close();
			
			Object ret = parser.parse(new InputStreamReader(conn.getInputStream()));
			
			if(ret instanceof JSONObject) {
				return (JSONObject)ret;
			} else {
				JSONObject tmp = new JSONObject();
				tmp.put("value", ret);
				return tmp;
			}
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
