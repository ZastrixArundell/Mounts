package com.github.zastrixarundell.mounts;

import org.bukkit.plugin.java.JavaPlugin;

public class Mounts extends JavaPlugin
{

    private static Mounts plugin;

    @Override
    public void onEnable()
    {
        super.onEnable();
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
    }

    public static Mounts getInstance()
    {
        return plugin;
    }

}
