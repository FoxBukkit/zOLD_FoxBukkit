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
package de.doridian.yiffbukkit.advanced.packetlistener;

import net.minecraft.server.v1_7_R3.Packet;
import org.bukkit.entity.Player;

interface YBPacketListenerInt {
	public boolean onOutgoingPacket(Player ply, Class<? extends Packet> packetCls, Packet packet);
	public boolean onIncomingPacket(Player ply, Class<? extends Packet> packetCls, Packet packet);
	@Deprecated
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet);
	@Deprecated
	public boolean onIncomingPacket(Player ply, int packetID, Packet packet);
}
