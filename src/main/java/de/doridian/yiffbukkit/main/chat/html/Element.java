package de.doridian.yiffbukkit.main.chat.html;

import de.doridian.yiffbukkit.main.chat.Parser;
import net.minecraft.server.v1_7_R1.ChatBaseComponent;
import net.minecraft.server.v1_7_R1.ChatClickable;
import net.minecraft.server.v1_7_R1.ChatComponentText;
import net.minecraft.server.v1_7_R1.ChatHoverable;
import net.minecraft.server.v1_7_R1.ChatModifier;
import net.minecraft.server.v1_7_R1.EnumClickAction;
import net.minecraft.server.v1_7_R1.EnumHoverAction;

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
	public List<ChatBaseComponent> getNmsComponents(ChatModifier style, boolean condenseElements) throws JAXBException {
		modifyStyle(style);

		if (onClick != null) {
			final Matcher matcher = FUNCTION_PATTERN.matcher(onClick);
			if (!matcher.matches()) {
				throw new RuntimeException("Invalid click handler");
			}

			final String eventType = matcher.group(1);
			final String eventString = matcher.group(2);
			final EnumClickAction enumClickAction = EnumClickAction.a(eventType.toLowerCase());
			if (enumClickAction == null) {
				throw new RuntimeException("Unknown click action "+eventType);
			}

			style.a(new ChatClickable(enumClickAction, eventString));
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

			style.a(new ChatHoverable(enumClickAction, Parser.parse(eventString)));
		}

		final List<ChatBaseComponent> components = new ArrayList<>();
		if (!condenseElements && (mixedContent.isEmpty() || mixedContent.get(0) instanceof Element))
			mixedContent.add(0, "");
		for (Object o : mixedContent) {
			if (o instanceof String) {
				final ChatComponentText component = new ChatComponentText((String) o);
				component.setChatModifier(style);
				components.add(component);
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

	public ChatBaseComponent getNmsComponent() throws JAXBException {
		return getNmsComponent(new ChatModifier());
	}

	public ChatBaseComponent getNmsComponent(ChatModifier style) throws JAXBException {
		return condense(getNmsComponents(style, false));
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
