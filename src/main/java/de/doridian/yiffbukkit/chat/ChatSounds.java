package de.doridian.yiffbukkit.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.main.util.Utils;

public class ChatSounds {
	private static final Map<String, String> chatSounds = new HashMap<String, String>();
	private static final Pattern wordPattern = Pattern.compile("([^-'\\p{L}]*)([-'\\p{L}]*)");
	static {
		chatSounds.put("meow", "mob.cat.meow");
		chatSounds.put("miau", "mob.cat.meow");
		chatSounds.put("purr", "mob.cat.purr");
		chatSounds.put("purrr", "mob.cat.purr");
		chatSounds.put("purrrr", "mob.cat.purr");
		chatSounds.put("prr", "mob.cat.purr");
		chatSounds.put("prrr", "mob.cat.purr");
		chatSounds.put("prrrr", "mob.cat.purr");

		chatSounds.put("woof", "mob.wolf.bark/0.4/0.8/1.2");
		chatSounds.put("wuff", "mob.wolf.bark/0.4/0.8/1.2");
		chatSounds.put("arf", "mob.wolf.bark/0.4/1.8/2.2");
		chatSounds.put("grr", "mob.wolf.growl");
		chatSounds.put("grrr", "mob.wolf.growl");
		chatSounds.put("grrrr", "mob.wolf.growl");
		chatSounds.put("grrrrr", "mob.wolf.growl");
		chatSounds.put("rawr", "mob.wolf.growl");
		chatSounds.put("howl", "mob.wolf.howl");
		chatSounds.put("welp", "mob.wolf.whine");
		chatSounds.put("yelp", "mob.wolf.whine");

		chatSounds.put("moo", "mob.cow");
		chatSounds.put("muh", "mob.cow");

		chatSounds.put("baa", "mob.sheep");
		chatSounds.put("baaa", "mob.sheep");
		chatSounds.put("baaaa", "mob.sheep");
		chatSounds.put("meh", "mob.sheep");

		chatSounds.put("oink", "mob.pig");

		chatSounds.put("hiss", "random.fuse/1.0/0.5/0.5");
		chatSounds.put("sss", "random.fuse/1.0/0.5/0.5");
		chatSounds.put("ssss", "random.fuse/1.0/0.5/0.5");
		chatSounds.put("sssss", "random.fuse/1.0/0.5/0.5");
		chatSounds.put("ssssss", "random.fuse/1.0/0.5/0.5");
		chatSounds.put("fff", "ambient.weather.rain/1.0/1.7/1.7");
		chatSounds.put("ffff", "ambient.weather.rain/1.0/1.7/1.7");
		chatSounds.put("fffff", "ambient.weather.rain/1.0/1.7/1.7");
		chatSounds.put("ffffff", "ambient.weather.rain/1.0/1.7/1.7");

		chatSounds.put("burp", "random.burp/0.5/0.9/1.0");
		chatSounds.put("psh", "random.fizz/0.5/2.0/3.0");

		chatSounds.put("yiff", "mob.slime");
		chatSounds.put("fap", "mob.wolf.shake/0.5/0.5/0.5");
		chatSounds.put("fapfap", "mob.wolf.shake/0.5/0.5/0.5");
		chatSounds.put("fapfapfap", "mob.wolf.shake/0.5/0.5/0.5");
	}

	public static String processMessage(final Player player, final String message) {
		// Split up words
		final List<String> prefixes = new ArrayList<String>();
		final List<String> words = new ArrayList<String>();

		final Matcher matcher = wordPattern.matcher(message);
		while (matcher.find()) {
			prefixes.add(matcher.group(1));
			words.add(matcher.group(2));
		}

		// Look for chat sounds
		for (String word : words) {
			String soundName = chatSounds.get(word.replaceAll("[-,.!?/]","").toLowerCase());
			if (soundName == null)
				continue;

			String[] split = soundName.split("/");
			if (split.length < 3)
				split = new String[] { soundName, "1.0", "0.8", "1.2" };

			soundName = split[0];
			float volume = Float.parseFloat(split[1]);
			float minPitch = Float.parseFloat(split[2]);
			float maxPitch = Float.parseFloat(split[3]);

			Utils.makeSound(player.getEyeLocation(), soundName, volume, (float) (minPitch + Math.random()*(maxPitch - minPitch)));
		}

		return message;
	}
}
