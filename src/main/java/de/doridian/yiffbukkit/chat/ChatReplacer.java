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
package de.doridian.yiffbukkit.chat;

import java.io.Serializable;
import java.util.regex.Pattern;

public interface ChatReplacer extends Serializable {
	public String replace(String msg);
	public String toString();
	public String asCommand();

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
		public String asCommand() {
			return "/crepl " + from + " " + to;
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

        public RegexChatReplacer(String from, String to, boolean ignoreCase) {
            this.from = Pattern.compile(from, ignoreCase ? Pattern.CASE_INSENSITIVE : 0);
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
		public String asCommand() {
			return "/crepl -r " + from + " " + to;
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
