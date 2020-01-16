package com.github.zastrixarundell.mounts.commands;

import com.github.zastrixarundell.mounts.Mounts;
import com.github.zastrixarundell.mounts.utils.GUIUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MountsCommand implements CommandExecutor
{

    private Mounts plugin;

    public MountsCommand(Mounts plugin)
    {
        this.plugin = plugin;
        plugin.getCommand("mounts").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args)
    {
        if (!(commandSender instanceof Player))
        {
            commandSender.sendMessage(Mounts.prefix + ChatColor.RED + "This command is only meant" +
                    " for players");

            return true;
        }

        Player sender = (Player) commandSender;

        GUIUtils.openGUI(sender, 1);

        return true;
    }
}
