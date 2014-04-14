package de.doridian.yiffbukkit.main.chat.html;

import net.minecraft.server.v1_7_R3.ChatClickable;
import net.minecraft.server.v1_7_R3.ChatModifier;
import net.minecraft.server.v1_7_R3.EnumClickAction;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class A extends Element {
	@XmlAttribute(required = true)
	private String href;

	@Override
	protected void modifyStyle(ChatModifier style) {
		style.setChatClickable(new ChatClickable(EnumClickAction.OPEN_URL, href));
	}
}
