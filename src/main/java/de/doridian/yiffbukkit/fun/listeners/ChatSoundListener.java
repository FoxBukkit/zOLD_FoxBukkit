package de.doridian.yiffbukkit.fun.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChatEvent;

import de.doridian.yiffbukkit.main.listeners.BaseListener;
import de.doridian.yiffbukkit.main.util.Utils;

public class ChatSoundListener extends BaseListener {
	private static final Map<String, String> chatSounds = new HashMap<String, String>();
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
		chatSounds.put("mäh", "mob.sheep");

		chatSounds.put("oink", "mob.pig");

		chatSounds.put("hiss", "random.fuse/1.0/0.5/0.5");
		chatSounds.put("sss", "random.fuse/1.0/0.5/0.5");
		chatSounds.put("ssss", "random.fuse/1.0/0.5/0.5");
		chatSounds.put("sssss", "random.fuse/1.0/0.5/0.5");
		chatSounds.put("ssssss", "random.fuse/1.0/0.5/0.5");
		chatSounds.put("fff", "random.fuse/1.0/0.5/0.5");
		chatSounds.put("ffff", "random.fuse/1.0/0.5/0.5");
		chatSounds.put("fffff", "random.fuse/1.0/0.5/0.5");
		chatSounds.put("ffffff", "random.fuse/1.0/0.5/0.5");

		chatSounds.put("burp", "random.burp/0.5/0.9/1.0");
		chatSounds.put("psh", "random.fizz/0.5/2.0/3.0");

		chatSounds.put("yiff", "mob.slime");
		chatSounds.put("fap", "mob.wolf.shake/0.5/0.5/0.5");
		chatSounds.put("fapfap", "mob.wolf.shake/0.5/0.5/0.5");
		chatSounds.put("fapfapfap", "mob.wolf.shake/0.5/0.5/0.5");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerChat(PlayerChatEvent event) {
		for (String part : event.getMessage().split(" ")) {
			String soundName = chatSounds.get(part.replaceAll("[-,.!?/]","").toLowerCase());
			if (soundName == null)
				continue;

			String[] split = soundName.split("/");
			if (split.length < 3)
				split = new String[] { soundName, "1.0", "0.8", "1.2" };

			soundName = split[0];
			float volume = Float.parseFloat(split[1]);
			float minPitch = Float.parseFloat(split[2]);
			float maxPitch = Float.parseFloat(split[3]);

			Utils.makeSound(event.getPlayer().getEyeLocation(), soundName, volume, (float) (minPitch + Math.random()*(maxPitch - minPitch)));
		}
	}
}
