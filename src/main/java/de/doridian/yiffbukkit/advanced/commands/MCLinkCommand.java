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
package de.doridian.yiffbukkit.advanced.commands;

import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.util.Utils;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

@ICommand.Names("mclink")
@ICommand.Help("Allows you to link your Minecraft account to your forums account")
@ICommand.Usage("")
@ICommand.Permission("yiffbukkit.mclink")
public class MCLinkCommand extends ICommand {
    @Override
    public void Run(final Player ply, String[] args, String argStr, String commandName) throws YiffBukkitCommandException {
        new Thread() {
            public void run() {
                try {
                    URL url = new URL("http://mc.doridian.de/mclink_int.php?scode=meowmeow18372385MCLINKER&uuid=" + Utils.URLEncode(ply.getUniqueId().toString()));
                    URLConnection conn = url.openConnection();
                    System.setProperty("http.agent", "");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(20000);

                    final String link = new BufferedReader(new InputStreamReader(conn.getInputStream())).readLine();

                    PlayerHelper.sendDirectedMessage(ply, "Go here to complete: " + link);
                } catch(Exception e) {
                    PlayerHelper.sendDirectedMessage(ply, "Internal error. Try again later");
                }
            }
        }.start();
    }
}
