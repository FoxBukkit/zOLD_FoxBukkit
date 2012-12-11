package de.doridian.yiffbukkit.mcbans;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.mcbans.commands.BanCommand;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericCheckBox;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericListWidget;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.Arrays;
import java.util.HashSet;

public class MCBansGUI extends GenericPopup {
	final YiffBukkit plugin;
	final SpoutPlayer player;

	final GenericListWidget playerList;
	final HashSet<GenericCheckBox> reasonCheckBoxes;
	final GenericTextField customReason;

	final GenericCheckBox global;
	final GenericCheckBox rollback;
	final GenericCheckBox unjail;

	public MCBansGUI(YiffBukkit plug, SpoutPlayer ply) {
		plugin = plug;
		player = ply;

		this.setHeight(this.getMaxHeight());
		this.setWidth(this.getMaxWidth());

		//Player list
		playerList = new GenericListWidget();
		for(SpoutPlayer otherply : SpoutManager.getOnlinePlayers()) {
			if(otherply.equals(ply)) continue;
			playerList.addItem(new ListWidgetItem(otherply.getName(), plugin.playerHelper.getPlayerRank(otherply)));
		}
		playerList.setAnchor(WidgetAnchor.TOP_LEFT);
		playerList.setHeight(this.getHeight());
		playerList.setWidth(100);
		this.attachWidget(plugin, playerList);

		//Reason selection
		reasonCheckBoxes = new HashSet<GenericCheckBox>();

		GenericLabel lbl = new GenericLabel();
		lbl.setText("Reason");
		lbl.setX(105);
		lbl.setY(5);
		lbl.setHeight(1);
		lbl.setWidth(1);
		this.attachWidget(plugin, lbl);

		HashSet<String> reasons = new HashSet<String>(Arrays.asList(new String[]{"grief", "xray", "fly", "speedhacking", "alt"}));
		GenericCheckBox cb = null;
		int yPos = 25;
		for(String reason: reasons) {
			cb = new GenericCheckBox();
			cb.setText(reason);
			cb.setAnchor(WidgetAnchor.TOP_LEFT);
			cb.setX(105);
			cb.setY(yPos);
			cb.setWidth(100);
			cb.setHeight(20);
			yPos += 25;
			reasonCheckBoxes.add(cb);
			this.attachWidget(plugin, cb);
		}

		customReason = new GenericTextField();
		customReason.setAnchor(WidgetAnchor.TOP_LEFT);
		customReason.setX(105);
		customReason.setY(yPos);
		customReason.setWidth(100);
		customReason.setHeight(20);
		this.attachWidget(plugin, customReason);

		//Ban attributes
		lbl = new GenericLabel();
		lbl.setText("Attributes");
		lbl.setX(210);
		lbl.setY(5);
		lbl.setHeight(1);
		lbl.setWidth(1);
		this.attachWidget(plugin, lbl);

		global = new GenericCheckBox();
		global.setText("Global");
		global.setWidth(100);
		global.setHeight(20);
		global.setX(210);
		global.setY(25);
		this.attachWidget(plugin, global);

		rollback = new GenericCheckBox();
		rollback.setText("Rollback");
		rollback.setWidth(100);
		rollback.setHeight(20);
		rollback.setX(210);
		rollback.setY(50);
		this.attachWidget(plugin, rollback);

		unjail = new GenericCheckBox();
		unjail.setText("Unjail");
		unjail.setWidth(100);
		unjail.setHeight(20);
		unjail.setX(210);
		unjail.setY(75);
		unjail.setChecked(true);
		this.attachWidget(plugin, unjail);

		GenericButton button = new GenericButton() {
			@Override
			public void onButtonClick(ButtonClickEvent event) {
				super.onButtonClick(event);
				doExec();
			}
		};
		button.setText("Ban");
		button.setX(210);
		button.setY(this.getHeight() - 25);
		button.setHeight(20);
		button.setWidth(40);
		this.attachWidget(plugin, button);

		button = new GenericButton() {
			@Override
			public void onButtonClick(ButtonClickEvent event) {
				super.onButtonClick(event);
				close();
			}
		};
		button.setText("Cancel");
		button.setX(260);
		button.setY(this.getHeight() - 25);
		button.setHeight(20);
		button.setWidth(40);
		this.attachWidget(plugin, button);

		ply.getMainScreen().attachPopupScreen(this);
	}

	public void doExec() {
		try {
			StringBuilder reason = new StringBuilder();
			for(GenericCheckBox cb : reasonCheckBoxes) {
				if(!cb.isChecked()) continue;
				reason.append(cb.getText());
				reason.append(' ');
			}
			reason.append(customReason.getText());
			BanCommand.executeBan(player, "\"" + playerList.getSelectedItem().getTitle() + "\"", reason.toString().trim(), plugin, unjail.isChecked(), rollback.isChecked(), global.isChecked(), null);
		} catch(YiffBukkitCommandException e) {
			PlayerHelper.sendDirectedMessage(player, "Error: " + e.getMessage());
		}
		close();
	}
}
