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
package de.doridian.yiffbukkit.main.chat;

import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.chat.html.Element;
import net.minecraft.server.v1_7_R3.ChatBaseComponent;
import net.minecraft.server.v1_7_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

public class Parser {
	/*
	 * ChatComponentText = TextComponent
	 * ChatMessage = TranslatableComponent
	 * ChatModifier = Style
	 * ChatClickable = ClickEvent
	 * ChatHoverable = HoverEvent
	 */
	public static ChatBaseComponent parse(String xmlSource, Object... params) throws JAXBException {
		xmlSource = "<span>" + xmlSource + "</span>";

		final JAXBContext jaxbContext = JAXBContext.newInstance(Element.class);
		final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		final Element element = (Element) unmarshaller.unmarshal(new StringReader(xmlSource));

		return element.getDefaultNmsComponent(params);
	}

	public static ChatBaseComponent format(String format, Object... params) throws JAXBException {
		return parse(format, params);
	}

	public static boolean sendToAll(String format, Object... params) {
		return sendToPlayers(Arrays.asList(Bukkit.getOnlinePlayers()), format, params);
	}

	public static boolean sendToPlayers(List<? extends CommandSender> targetPlayers, String format, Object... params) {
		try {
			final PacketPlayOutChat packet = createChatPacket(format, params);

			for (CommandSender commandSender : targetPlayers) {
				if (!(commandSender instanceof Player)) {
					commandSender.sendMessage(parsePlain(format, params));
					continue;
				}

				PlayerHelper.sendPacketToPlayer((Player) commandSender, packet);
			}

			return true;
		}
		catch (JAXBException e) {
			e.printStackTrace();
			Bukkit.broadcastMessage("Error parsing XML");

			return false;
		}
	}

	public static boolean sendToPlayer(Player player, String format, Object... params) {
		try {
			PlayerHelper.sendPacketToPlayer(player, createChatPacket(format, params));

			return true;
		} catch (JAXBException e) {
			e.printStackTrace();
			player.sendMessage("Error parsing XML");

			return false;
		}
	}

	public static boolean sendToPlayer(CommandSender commandSender, String format, Object... params) {
		if (commandSender instanceof Player)
			return sendToPlayer((Player) commandSender, format, params);

		commandSender.sendMessage(parsePlain(format, params));
		return true;
	}

	private static String parsePlain(String format, Object[] params) {
		return String.format(format, params); // TODO: strip XML tags
	}

	private static PacketPlayOutChat createChatPacket(String format, Object... params) throws JAXBException {
		return new PacketPlayOutChat(format(format, params));
	}

	public static String escape(String s) {
		s = s.replace("&", "&amp;");
		s = s.replace("\"", "&quot;");
		s = s.replace("'", "&apos;");
		s = s.replace("<", "&lt;");
		s = s.replace(">", "&gt;");

		return s;
	}
}
