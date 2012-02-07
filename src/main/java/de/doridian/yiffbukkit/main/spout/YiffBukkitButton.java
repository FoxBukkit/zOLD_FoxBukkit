package de.doridian.yiffbukkit.main.spout;

import de.doridian.yiffbukkitsplit.YiffBukkitCommandException;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;

public abstract class YiffBukkitButton extends GenericButton {
	public YiffBukkitButton(String text) {
		super(text);
	}
	
	@Override
	public void onButtonClick(ButtonClickEvent event) {
		try {
			onClicked(event);
		}
		catch(YiffBukkitCommandException e) {
			event.getPlayer().sendMessage("[YBSC] " + e.getMessage());
			//e.printStackTrace();
		}
	}
	
	public abstract void onClicked(ButtonClickEvent event) throws YiffBukkitCommandException;
}
