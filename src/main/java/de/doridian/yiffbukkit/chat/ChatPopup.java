package de.doridian.yiffbukkit.chat;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.ContainerType;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericContainer;
//import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.spout.YiffBukkitButton;

public class ChatPopup extends GenericContainer {
	YiffBukkit plugin;
	//GenericContainer cont;
	SpoutPlayer player;
	
	public ChatPopup(SpoutPlayer ply, YiffBukkit plug) {
		super();
		
		plugin = plug;
		player = ply;
	}
	
	public void init() {
		this.setVisible(true);
		
		//cont = new GenericContainer();
		//cont.setAnchor(WidgetAnchor.BOTTOM_LEFT);
		
		String plyname = player.getName().toLowerCase();
		ChatHelper helper = ChatHelper.getInstance();
		for(ChatChannel chan : helper.container.channels.values()) {
			if(chan.players.containsKey(plyname)) {
				addTab(chan);
			}
		}
		
		this.setLayout(ContainerType.HORIZONTAL);
		//this.setAlign(WidgetAnchor.BOTTOM_LEFT);
		this.updateLayout();
		this.updateSize();
		
		//this.attachWidget(plugin, cont);
	}
	
	public void addTab(ChatChannel chanx) {
		final ChatChannel chan = chanx;
		YiffBukkitButton but = new YiffBukkitButton(chan.name) {
			@Override
			public void onClicked(ButtonClickEvent event) throws YiffBukkitCommandException {
				ChatHelper.getInstance().setActiveChannel(event.getPlayer(), chan);
			}
		};
		addTab(but);
	}
	
	public void addTab(GenericButton button) {
		button.setWidth(40).setHeight(20);
		//button.setX(0).setY(0);
		//button.setAnchor(WidgetAnchor.BOTTOM_LEFT);
		button.setAuto(true);
		button.setColor(new Color(1.0F, 1.0F, 1.0F, 1.0F));
		button.setHoverColor(new Color(1.0F, 0, 0, 1.0F));
		button.updateSize();
		this.addChild(button);
	}
}
