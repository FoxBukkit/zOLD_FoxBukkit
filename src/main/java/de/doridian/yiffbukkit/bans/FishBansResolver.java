package de.doridian.yiffbukkit.bans;

import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FishBansResolver {
	public static HashMap<String, Integer> getBanCounts(String username) {
		try {
			HttpURLConnection httpURLConnection = (HttpURLConnection)new URL("http://api.fishbans.com/bans/" + username).openConnection();
			httpURLConnection.setConnectTimeout(5000);
			httpURLConnection.setReadTimeout(5000);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(httpURLConnection.getInputStream()));
			//->bans->service->[key]->bans=[value]
			JSONObject serviceBans = (JSONObject)((JSONObject)jsonObject.get("bans")).get("service");
			HashMap<String, Integer> result = new HashMap<>();
			for(Object banEntry : serviceBans.entrySet()) {
				Map.Entry actualBanEntry = (Map.Entry)banEntry;
				result.put((String)actualBanEntry.getKey(), (int)(long)(Long)(((JSONObject)actualBanEntry.getValue()).get("bans")));
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<>();
		}
	}

	private static final HashMap<String, UUID> playerUUIDMap = new HashMap<>();

	private static final HttpProfileRepository HTTP_PROFILE_REPOSITORY = new HttpProfileRepository("minecraft");

	public static UUID getUUID(String username) {
		UUID ret = playerUUIDMap.get(username.toLowerCase());
		if(ret != null)
			return ret;
		try {
			Profile[] profiles = HTTP_PROFILE_REPOSITORY.findProfilesByNames(username);
			if(profiles.length == 1) {
				ret = UUID.fromString(profiles[0].getId());
				playerUUIDMap.put(username.toLowerCase(), ret);
				return ret;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
