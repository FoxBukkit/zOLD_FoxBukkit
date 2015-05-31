/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.foxelbox.foxbukkit.chat.html;

import com.foxelbox.foxbukkit.chat.HTMLParser;
import net.minecraft.server.v1_8_R3.ChatBaseComponent;
import net.minecraft.server.v1_8_R3.ChatClickable;
import net.minecraft.server.v1_8_R3.ChatHoverable;
import net.minecraft.server.v1_8_R3.ChatModifier;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;

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
	public List<ChatBaseComponent> getNmsComponents(ChatModifier style, boolean condenseElements) throws Exception {
		modifyStyle(style);

		if (onClick != null) {
			final Matcher matcher = FUNCTION_PATTERN.matcher(onClick);
			if (!matcher.matches()) {
				throw new RuntimeException("Invalid click handler");
			}

			final String eventType = matcher.group(1);
			final String eventString = matcher.group(2);
			final ChatClickable.EnumClickAction enumClickAction = ChatClickable.EnumClickAction.a(eventType.toLowerCase());
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
			final ChatHoverable.EnumHoverAction enumClickAction = ChatHoverable.EnumHoverAction.a(eventType.toLowerCase());
			if (enumClickAction == null) {
				throw new RuntimeException("Unknown click action "+eventType);
			}

			style.setChatHoverable(new ChatHoverable(enumClickAction, HTMLParser.parse(eventString)));
		}

		final List<ChatBaseComponent> components = new ArrayList<>();
		if (!condenseElements)
			mixedContent.add(0, "");
		for (Object o : mixedContent) {
			if (o instanceof String) {
				for (IChatBaseComponent baseComponent : CraftChatMessage.fromString(((String)o).replace('\u000B', ' '), style.clone())) {
					components.add((ChatBaseComponent) baseComponent);
				}
			}
			else if (o instanceof Element) {
				final Element element = (Element) o;
				if (condenseElements) {
					components.add(element.getNmsComponent(style.clone()));
				}
				else {
					components.addAll(element.getNmsComponents(style.clone(), false));
				}
			}
			else {
				throw new RuntimeException(o.getClass().toString());
			}
		}

		return components;
	}

	public ChatBaseComponent getDefaultNmsComponent() throws Exception {
		return getNmsComponent(new ChatModifier());
	}

	public ChatBaseComponent getNmsComponent(ChatModifier style) throws Exception {
		return condense(getNmsComponents(style, false));
	}

	private static ChatBaseComponent condense(List<ChatBaseComponent> components) {
		if (components.isEmpty()) {
			return null;
		}

		components = new ArrayList<>(components);

		final ChatBaseComponent head = components.remove(0);

		if (!components.isEmpty()) {
			head.a = (List<IChatBaseComponent>)(List)components;
		}

		return head;
	}
}
