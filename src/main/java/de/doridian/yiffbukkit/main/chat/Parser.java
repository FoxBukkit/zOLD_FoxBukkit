package de.doridian.yiffbukkit.main.chat;

import de.doridian.yiffbukkit.main.chat.html.Element;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import net.minecraft.server.v1_7_R1.ChatBaseComponent;
import net.minecraft.server.v1_7_R1.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

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

	public static void sendToAll(String format, Object... params) throws JAXBException {
		final ChatBaseComponent component = format(format, params);
		final PacketPlayOutChat packet = new PacketPlayOutChat(component);

		for (Player player : Bukkit.getOnlinePlayers()) {
			PlayerHelper.sendPacketToPlayer(player, packet);
		}
	}
}
