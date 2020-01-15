package com.github.zastrixarundell.mounts;

import com.github.zastrixarundell.mounts.commands.MountsCommand;
import com.github.zastrixarundell.mounts.listeners.MountStateListener;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Mounts extends JavaPlugin
{

    private static Mounts plugin;

    public static final String prefix =
            ChatColor.GRAY + "[" + ChatColor.AQUA + "Mounts" + ChatColor.GRAY + "] " + ChatColor.RESET;

    @Override
    public void onEnable()
    {
        plugin = this;
        new MountsCommand(this);
        new MountStateListener(this);
    }

    @Override
    public void onDisable()
    {

    }

    public static Mounts getInstance()
    {
        return plugin;
    }

}
