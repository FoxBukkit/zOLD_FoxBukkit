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
import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.*;
import de.doridian.yiffbukkit.main.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Names("cexec")
@Help("Runs a command as another Player (WARNING: Will be chat if no / infront!)")
@Usage("<name> <command>")
@Permission("yiffbukkit.exec.otherclient")
@AbusePotential
public class ClientExecCommand extends ICommand {
    @Override
    public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws YiffBukkitCommandException {
        Player target = playerHelper.matchPlayerSingle(args[0]);
        if(PlayerHelper.getPlayerLevel(commandSender) < PlayerHelper.getPlayerLevel(target))
            throw new PermissionDeniedException();

        String command = Utils.concatArray(args, 1, "");

        target.chat(command);
    }
}
