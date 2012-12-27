package de.doridian.yiffbukkit.advanced.commands;

import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ICommand.Names("cexec")
@ICommand.Help("Runs a command as another Player (WARNING: Will be chat if no / infront!)")
@ICommand.Usage("<name> <command>")
@ICommand.Permission("yiffbukkit.exec.otherclient")
public class ClientExecCommand extends ICommand {
    @Override
    public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
        Player target = playerHelper.matchPlayerSingle(args[0]);
        if(playerHelper.getPlayerLevel(commandSender) < playerHelper.getPlayerLevel(target))
            throw new PermissionDeniedException();

        String command = Utils.concatArray(args, 1, "");

        target.chat(command);
    }
}
