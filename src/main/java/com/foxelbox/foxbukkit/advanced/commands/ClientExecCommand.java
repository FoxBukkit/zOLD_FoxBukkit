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
package com.foxelbox.foxbukkit.advanced.commands;

import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.PermissionDeniedException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.*;
import com.foxelbox.foxbukkit.main.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Names("cexec")
@Help("Runs a command as another Player (WARNING: Will be chat if no / infront!)")
@Usage("<name> <command>")
@Permission("foxbukkit.exec.otherclient")
@AbusePotential
public class ClientExecCommand extends ICommand {
    @Override
    public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
        Player target = playerHelper.matchPlayerSingle(args[0]);
        if(PlayerHelper.getPlayerLevel(commandSender) < PlayerHelper.getPlayerLevel(target))
            throw new PermissionDeniedException();

        String command = Utils.concatArray(args, 1, "");

        target.chat(command);
    }
}
