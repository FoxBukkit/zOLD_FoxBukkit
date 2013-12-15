package de.doridian.yiffbukkit.main.chat.html;

import net.minecraft.server.v1_7_R1.ChatModifier;
import net.minecraft.server.v1_7_R1.EnumChatFormat;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement
public class Color extends Element {
	@XmlAttribute
	private String name;

	@XmlAttribute
	private String id;

	@Override
	protected void modifyStyle(ChatModifier style) {
		if (name != null) {
			style.setColor(EnumChatFormat.b(name.toUpperCase())); // v1_7_R1
		}

		if (id != null && !id.isEmpty()) {
			@SuppressWarnings("unchecked")
			final Map<Character, EnumChatFormat> idToChatFormat = EnumChatFormat.w; // v1_7_R1
			style.setColor(idToChatFormat.get(id.charAt(0)));
		}
	}
}
