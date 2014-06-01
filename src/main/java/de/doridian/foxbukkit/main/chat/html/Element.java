/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.foxbukkit.main.chat.html;

import de.doridian.foxbukkit.main.chat.Parser;
import net.minecraft.server.v1_7_R3.ChatBaseComponent;
import net.minecraft.server.v1_7_R3.ChatClickable;
import net.minecraft.server.v1_7_R3.ChatHoverable;
import net.minecraft.server.v1_7_R3.ChatModifier;
import net.minecraft.server.v1_7_R3.EnumClickAction;
import net.minecraft.server.v1_7_R3.EnumHoverAction;
import net.minecraft.server.v1_7_R3.IChatBaseComponent;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@XmlSeeAlso({
		A.class,
		B.class,
		Color.class,
		I.class,
		Obfuscated.class,
		S.class,
		Span.class,
		Tr.class,
		U.class,
})
public abstract class Element {
	@XmlAttribute
	private String onClick = null;

	@XmlAttribute
	private String onHover = null;

	@XmlElementRef(type = Element.class)
	@XmlMixed
	private List<Object> mixedContent = new ArrayList<>();

	protected abstract void modifyStyle(ChatModifier style);

	private static final Pattern FUNCTION_PATTERN = Pattern.compile("^([^(]+)\\('(.*)'\\)$");
	public List<ChatBaseComponent> getNmsComponents(ChatModifier style, boolean condenseElements, Object[] params) throws JAXBException {
		modifyStyle(style);

		if (onClick != null) {
			final Matcher matcher = FUNCTION_PATTERN.matcher(onClick);
			if (!matcher.matches()) {
				throw new RuntimeException("Invalid click handler");
			}

			final String eventType = matcher.group(1);
			final String eventString = String.format(matcher.group(2), params);
			final EnumClickAction enumClickAction = EnumClickAction.a(eventType.toLowerCase());
			if (enumClickAction == null) {
				throw new RuntimeException("Unknown click action "+eventType);
			}

			style.setChatClickable(new ChatClickable(enumClickAction, eventString));
		}

		if (onHover != null) {
			final Matcher matcher = FUNCTION_PATTERN.matcher(onHover);
			if (!matcher.matches()) {
				throw new RuntimeException("Invalid hover handler");
			}

			final String eventType = matcher.group(1);
			final String eventString = matcher.group(2);
			final EnumHoverAction enumClickAction = EnumHoverAction.a(eventType.toLowerCase());
			if (enumClickAction == null) {
				throw new RuntimeException("Unknown click action "+eventType);
			}

			style.a(new ChatHoverable(enumClickAction, Parser.parse(eventString, params)));
		}

		final List<ChatBaseComponent> components = new ArrayList<>();
		if (!condenseElements)
			mixedContent.add(0, "");
		for (Object o : mixedContent) {
			if (o instanceof String) {
				for (IChatBaseComponent baseComponent : CraftChatMessage.fromString(String.format((String) o, params), style.clone())) {
					components.add((ChatBaseComponent) baseComponent);
				}
			}
			else if (o instanceof Element) {
				final Element element = (Element) o;
				if (condenseElements) {
					components.add(element.getNmsComponent(style.clone(), params));
				}
				else {
					components.addAll(element.getNmsComponents(style.clone(), false, params));
				}
			}
			else {
				throw new RuntimeException(o.getClass().toString());
			}
		}

		return components;
	}

	public ChatBaseComponent getDefaultNmsComponent(Object[] params) throws JAXBException {
		return getNmsComponent(new ChatModifier(), params);
	}

	public ChatBaseComponent getNmsComponent(ChatModifier style, Object[] params) throws JAXBException {
		return condense(getNmsComponents(style, false, params));
	}

	private static ChatBaseComponent condense(List<ChatBaseComponent> components) {
		if (components.isEmpty()) {
			return null;
		}

		components = new ArrayList<>(components);

		final ChatBaseComponent head = components.remove(0);

		if (!components.isEmpty()) {
			head.a = components;
		}

		return head;
	}
}
