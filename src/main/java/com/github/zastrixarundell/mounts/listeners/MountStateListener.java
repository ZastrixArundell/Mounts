package com.github.zastrixarundell.mounts.listeners;

import com.github.zastrixarundell.mounts.Mounts;
import com.github.zastrixarundell.mounts.entities.Mount;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;

public class MountStateListener implements Listener
{

    private Mounts plugin;

    public MountStateListener(Mounts plugin)
    {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void handleDismountEvent(EntityDismountEvent event)
    {
        if (!(event.getEntity() instanceof Horse))
            return;

        Horse horse = (Horse) event.getEntity();

        if(Mount.isMount(horse))
            horse.remove();
    }
}
