/**
 * This file is part of YiffBukkit.
 *
 * YiffBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * YiffBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with YiffBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.yiffbukkit.bans;

import de.doridian.yiffbukkit.bans.listeners.BansPlayerListener;
import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.offlinebukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Bans {
	private YiffBukkit plugin;

	public LockDownMode lockdownMode = LockDownMode.OFF;

	@SuppressWarnings("unused")
	private BansPlayerListener playerListener;

	public Bans(YiffBukkit plug) {
		plugin = plug;
		playerListener = new BansPlayerListener();
	}


	public enum BanType {
		GLOBAL("global"), LOCAL("local"), TEMPORARY("temp");

		private final String name;

		BanType(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}

	public void unban(final CommandSender from, final String ply) {
		new Thread() {
			public void run() {
				Ban ban = BanResolver.getBan(ply, null, false);
				if(ban != null) {
					BanResolver.deleteBan(ban);
					PlayerHelper.sendServerMessage(from.getName() + " unbanned " + ply + "!");
				} else {
					PlayerHelper.sendDirectedMessage(from, "Player with the name " + ply + " was not banned!");
				}
			}
		}.start();
	}

	public void ban(final CommandSender from, final Player ply, final String reason, final BanType type) {
		if (type == BanType.TEMPORARY) return;
		ban(from, ply, reason, type, 0, "");
	}

	public void ban(final CommandSender from, final Player ply, final String reason, final BanType type, final long duration, final String measure) {
		final String addr;
		if (ply instanceof OfflinePlayer) {
			addr = "";
		} else {
			addr = ply.getAddress().getAddress().getHostAddress();
		}
		ban(from, ply.getName(), ply.getUniqueId(), addr, reason, type, duration, measure);
	}

	public void ban(final CommandSender from, final String plyName, final UUID plyUUID, final String ip, final String reason, final BanType type) {
		if (type == BanType.TEMPORARY) return;
		ban(from, plyName, plyUUID, ip, reason, type, 0, "");
	}

	public void ban(final CommandSender from, final String _plyName, final UUID plyUUID, final String ip, final String reason, final BanType type, final long duration, final String measure) {
		if (type == null) return;
		if (type == BanType.TEMPORARY) return;

		final String plyName;
		if(_plyName == null)
			plyName = YiffBukkit.instance.playerHelper.getPlayerByUUID(plyUUID).getName();
		else
			plyName = _plyName;

		new Thread() {
			public void run() {
				Ban newBan = new Ban();
				newBan.setUser(plyName, plyUUID);
				newBan.setAdmin(from.getName(), from.getUniqueId());
				newBan.setReason(reason);
				newBan.setType(type.getName());
				BanResolver.addBan(newBan);
				PlayerHelper.sendServerMessage(from.getName() + " banned " + plyName + " [Reason: " + reason + "]!");
			}
		}.start();
	}
}