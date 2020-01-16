package com.github.zastrixarundell.mounts.listeners;

import com.github.zastrixarundell.mounts.Mounts;
import com.github.zastrixarundell.mounts.entities.Mount;
import com.github.zastrixarundell.mounts.entities.Rider;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;

public class PlayerEventListener implements Listener
{

    private Mounts plugin;

    public PlayerEventListener(Mounts plugin)
    {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void removeMountsOnLogout(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();

        if(!player.isInsideVehicle())
            return;

        if(!(player.getVehicle() instanceof Horse))
            return;

        Horse horse = (Horse) player.getVehicle();

        if(Mount.isMount(horse))
            horse.remove();
    }

    @EventHandler
    private void removePlayerBufferOnQuit(PlayerQuitEvent event) { Rider.deleteRiderBuffer(event.getPlayer()); }

    @EventHandler
    private void disableGuiClick(InventoryClickEvent event)
    {
        InventoryHolder holder = event.getInventory().getHolder();

        if(!(holder instanceof Horse))
            return;

        Horse horse = (Horse) holder;

        if(Mount.isMount(horse))
        {
            event.setCancelled(true);

            if (event.getWhoClicked() instanceof Player)
                ((Player) event.getWhoClicked()).updateInventory();
        }
    }
}
