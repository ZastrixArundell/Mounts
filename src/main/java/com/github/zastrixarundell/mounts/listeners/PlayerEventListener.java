package com.github.zastrixarundell.mounts.listeners;

import com.github.zastrixarundell.mounts.Mounts;
import com.github.zastrixarundell.mounts.entities.Mount;
import com.github.zastrixarundell.mounts.entities.Rider;
import com.github.zastrixarundell.mounts.values.MountType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

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
    private void disableGuiHorseClick(InventoryClickEvent event)
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

    @EventHandler
    private void disableGuiMenuClick(InventoryClickEvent event)
    {
        if(!event.getView().getTitle().startsWith("Your mounts - page"))
            return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        player.updateInventory();

        if (event.getCurrentItem() == null)
            return;

        if (event.getCurrentItem().getItemMeta() == null)
            return;

        ItemStack item = event.getCurrentItem();

        if(item.getType() != Material.SADDLE)
            return;

        String rawName = ChatColor.stripColor(item.getItemMeta().getDisplayName().replace(" ", "_").toUpperCase());

        player.closeInventory();

        Rider rider = Rider.asRider(((Player) event.getWhoClicked()));

        new Mount(player, rider.getSpeed(), MountType.valueOf(rawName)).spawn();
    }
}
