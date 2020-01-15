package com.github.zastrixarundell.mounts.commands;

import com.github.zastrixarundell.mounts.Mounts;
import com.github.zastrixarundell.mounts.entities.Mount;
import com.github.zastrixarundell.mounts.entities.Rider;
import com.github.zastrixarundell.mounts.values.MountType;
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

        if (args.length < 1)
        {
            sender.sendMessage(Mounts.prefix + ChatColor.RED + "You need to input the mount name!");
            return true;
        }

        String name = args[0];

        try
        {
            Rider rider = Rider.asRider(sender);
            new Mount(sender, rider.getSpeed(), MountType.valueOf(name)).spawn();
            sender.sendMessage(Mounts.prefix + ChatColor.GREEN + "Spawned with speed of: " + rider.getSpeed());
        }
        catch (Exception e)
        {
            sender.sendMessage(Mounts.prefix + ChatColor.RED + "An error happened, check the name!");
            return true;
        }

        return true;
    }
}
