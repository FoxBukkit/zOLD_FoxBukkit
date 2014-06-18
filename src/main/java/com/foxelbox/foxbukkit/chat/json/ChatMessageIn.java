package com.foxelbox.foxbukkit.chat.json;

import com.foxelbox.foxbukkit.core.FoxBukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R3.command.CraftConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ChatMessageIn {
    public ChatMessageIn(CommandSender commandSender) {
        if(commandSender instanceof Player)
            this.from = new UserInfo(commandSender.getUniqueId(), commandSender.getName());
        else
            this.from = new UserInfo(CraftConsoleCommandSender.CONSOLE_UUID, commandSender.getName());
    }

    public ChatMessageIn() {
        this.timestamp = System.currentTimeMillis() / 1000;
        this.context = UUID.randomUUID();
        this.server = FoxBukkit.instance.configuration.getValue("server-name", "Main");
    }

    public String server;

    public UserInfo from;

    public long timestamp;
    public UUID context;

    public String type;
    public String contents;
}
