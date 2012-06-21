package de.doridian.yiffbukkit.chat;

import java.io.Serializable;
import java.util.regex.Pattern;

public interface ChatReplacer extends Serializable {
	public String replace(String msg);
	public String toString();

	class PlainChatReplacer implements ChatReplacer {
		private static final long serialVersionUID = 1L;

		private final String from;
		private final String to;

		public PlainChatReplacer(String from, String to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public String replace(String msg) {
			return msg.replace(from, to);
		}

		@Override
		public String toString() {
			return "[Plain] " + from + " => " + to;
		}

		@Override
		public int hashCode() {
			return (from.hashCode() / 2) + (to.hashCode() / 2);
		}

		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof PlainChatReplacer)) return false;
			PlainChatReplacer otherRepl = (PlainChatReplacer)obj;
			return otherRepl.from.equals(from) && otherRepl.to.equals(to);
		}
	}

	class RegexChatReplacer implements ChatReplacer {
		private static final long serialVersionUID = 1L;

		private final Pattern from;
		private final String to;

		public RegexChatReplacer(String from, String to) {
			this.from = Pattern.compile(from);
			this.to = to;
		}

		@Override
		public String replace(String msg) {
			return from.matcher(msg).replaceAll(to);
		}

		@Override
		public String toString() {
			return "[RegExp] " + from + " => " + to;
		}

		@Override
		public int hashCode() {
			return (from.hashCode() / 2) + (to.hashCode() / 2);
		}

		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof RegexChatReplacer)) return false;
			RegexChatReplacer otherRepl = (RegexChatReplacer)obj;
			return otherRepl.from.equals(from) && otherRepl.to.equals(to);
		}
	}
}
