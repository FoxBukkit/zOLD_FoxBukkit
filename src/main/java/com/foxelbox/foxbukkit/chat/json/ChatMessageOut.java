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
package com.foxelbox.foxbukkit.chat.json;

import java.util.UUID;

public class ChatMessageOut {
    public ChatMessageOut(String server, UserInfo from, String plain) {
        this.server = server;
        this.from = from;
        this.to = new MessageTarget("all", null);
        this.contents = new MessageContents(plain);
        this.context = UUID.randomUUID();
    }

    public ChatMessageOut(ChatMessageIn messageIn) {
        this(messageIn.server, messageIn.from, messageIn.contents);
        this.context = messageIn.context;
    }

    public String server;
    public UserInfo from;
    public MessageTarget to;

    public long timestamp = System.currentTimeMillis() / 1000;
    public UUID context;

    public MessageContents contents;
}
