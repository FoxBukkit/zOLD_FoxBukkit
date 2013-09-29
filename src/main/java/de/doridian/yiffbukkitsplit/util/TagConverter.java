package de.doridian.yiffbukkitsplit.util;

import de.doridian.yiffbukkit.main.util.RedisManager;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagConverter {
	public static void main(String[] args) {
		final boolean clean = "".isEmpty();

		final Map<String,String> oldPlayerTags = RedisManager.createKeptMap("playertags");
		final Map<String,String> playerTags = RedisManager.createKeptMap("playerTags");
		final Map<String,String> playerGroups = RedisManager.createKeptMap("playergroups");
		final Map<String,String> playerRankTags = RedisManager.createKeptMap("playerRankTags");
		final Map<String,String> rankTags = RedisManager.createKeptMap("ranktags");
		final Pattern pattern = Pattern.compile("^(.*)(\u00a7.)$");

		if (clean) {
			playerTags.clear();
			playerRankTags.clear();
		}

		for (Map.Entry<String, String> entry : oldPlayerTags.entrySet()) {
			final String playerName = entry.getKey().toLowerCase();
			final String oldPlayerTag = entry.getValue();

			String playerRankTag;
			String playerTag;

			final Matcher matcher = pattern.matcher(oldPlayerTag);
			if (matcher.matches()) {
				playerTag = matcher.group(1);
				playerRankTag = matcher.group(2);
			}
			else {
				playerTag = oldPlayerTag;
				playerRankTag = null;
			}

			playerTag = playerTag.trim();

			// Retrieve default rank tag
			String rank = playerGroups.get(playerName);
			if (rank == null)
				rank = "guest";

			String rankTag = rankTags.get(rank);
			if (rankTag == null)
				rankTag = "\u00a77";

			// Get rid of useless tags
			if (rankTag.equals(playerRankTag))
				playerRankTag = null;

			if (playerTag.isEmpty())
				playerTag = null;

			// Store new tags
			if (playerRankTag == null) {
				if (!clean)
					playerRankTags.remove(playerName);
			} else {
				playerRankTags.put(playerName, playerRankTag);
			}

			if (playerTag == null) {
				if (!clean)
					playerTags.remove(playerName);
			} else {
				playerTags.put(playerName, playerTag);
			}

			System.out.printf("%s: '%s' => '%s'/'%s'\n", playerName, oldPlayerTag, playerTag, playerRankTag);
		}
	}
}
