package de.doridian.yiffbukkit.bans;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FishBansResolver {
	public static HashMap<String, Integer> getBanCounts(String username) {
		try {
			HttpURLConnection httpURLConnection = (HttpURLConnection)new URL("http://api.fishbans.com/bans/" + username).openConnection();
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(httpURLConnection.getInputStream()));
			//->bans->service->[key]->bans=[value]
			JSONObject serviceBans = (JSONObject)((JSONObject)jsonObject.get("bans")).get("service");
			HashMap<String, Integer> result = new HashMap<>();
			for(Object banEntry : serviceBans.entrySet()) {
				Map.Entry actualBanEntry = (Map.Entry)banEntry;
				result.put((String)actualBanEntry.getKey(), (int)(long)(Long)(((JSONObject)actualBanEntry.getValue()).get("bans")));
				//System.out.println("A " + (String)actualBanEntry.getKey());
				//System.out.println("B " + (Integer)(((JSONObject)actualBanEntry.getValue()).get("bans")));
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<>();
		}
	}
}