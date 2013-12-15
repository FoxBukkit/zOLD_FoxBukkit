package de.doridian.yiffbukkit.main.chat;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.chat.html.Element;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import net.minecraft.server.v1_7_R1.ChatBaseComponent;
import net.minecraft.server.v1_7_R1.PacketPlayOutChat;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.xml.sax.SAXParseException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
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
	public static void main(String[] args) throws Exception {
		final String xmlSource = "<tr key=\"chat.type.achievement\"><span onClick=\"suggest_command('/msg Biohazard_Stuff ')\">Biohazard_Stuff</span><color id=\"a\" onHover=\"show_achievement('&lt;span>achievement.cookFish&lt;/span>')\">[<tr key=\"achievement.cookFish\"/>]</color></tr>";
		//final String xmlSource = "<span onClick=\"suggest_command('/pm TomyLobo ')\">aaaa</span>";

		System.out.println(xmlSource);

		try {
			System.out.println(parse(xmlSource).toString().replaceAll("TextComponent", "\nTextComponent"));
		}
		catch (UnmarshalException e) {
			final int maxLength = 120;
			final Throwable linkedException = e.getLinkedException();
			if (!(linkedException instanceof SAXParseException)) {
				throw e;
			}

			final SAXParseException saxParseException = (SAXParseException) linkedException;
			final int columnNumber = saxParseException.getColumnNumber() - 1;
			final int offset = Math.max(0, columnNumber - maxLength / 2);

			System.out.println(xmlSource.substring(Math.min(xmlSource.length() - 1, offset), Math.min(xmlSource.length() - 1, offset + maxLength)));
			System.out.println(StringUtils.repeat(" ", columnNumber - offset) + "^");
			System.out.flush();

			throw e;
		}
	}

	public static ChatBaseComponent parse(String xmlSource) throws JAXBException {
		if (xmlSource.charAt(0) != '<') {
			return parse("<span>" + xmlSource + "</span>");
		}

		final JAXBContext jaxbContext = JAXBContext.newInstance(Element.class);
		final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		final Element element = (Element) unmarshaller.unmarshal(new StringReader(xmlSource));

		return element.getNmsComponent();
	}

	public static void send(String xmlSource, Player... players) throws YiffBukkitCommandException {
		try {
			final ChatBaseComponent component = parse(xmlSource);
			System.out.println(component);
			final PacketPlayOutChat packet = new PacketPlayOutChat(component);
			for (Player player : players) {
				PlayerHelper.sendPacketToPlayer(player, packet);
			}
		}
		catch (JAXBException e) {
			e.printStackTrace();
			throw new YiffBukkitCommandException("Exception occurred while parsing", e);
		}
	}
}
