package de.doridian.yiffbukkit.main.chat.html;

import net.minecraft.server.v1_7_R3.ChatModifier;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class S extends Element {
	@Override
	protected void modifyStyle(ChatModifier style) {
		style.setStrikethrough(true);
	}
}
