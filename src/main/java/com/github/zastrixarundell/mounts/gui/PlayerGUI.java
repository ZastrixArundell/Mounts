package com.github.zastrixarundell.mounts.gui;

import com.github.zastrixarundell.mounts.Mounts;
import com.github.zastrixarundell.mounts.entities.Mount;
import com.github.zastrixarundell.mounts.entities.Rider;
import com.github.zastrixarundell.mounts.values.MountRace;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * General class regarding the player's GUI so they can select mounts and
 * listeners according to that GUI.
 */
public class PlayerGUI implements Listener
{

    public PlayerGUI(Mounts plugin)
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Open the GUI for the player which shows all of the available mounts.
     * @param player The player to whom the GUI will be opened.
     * @param page The index of the page.
     */
    public static void openMountsToPlayer(Player player, int page)
    {
        page = Math.max(page, 1);

        Optional<Rider> riderOptional = Rider.asRider(player);

        if(!riderOptional.isPresent())
            return;

        Rider rider = riderOptional.get();

        List<Mount> mounts = rider.getMounts();
        Inventory inventory = Bukkit.createInventory(player, 54, "Your mounts - page " + page);

        getMountsForPageIndex(mounts, page).forEach(mountType -> inventory.addItem(mountType.getRace().asItem()));

        player.openInventory(inventory);
    }

    /**
     * Gets the mounts which should be displayed on that specific page index.
     * @param mounts The list of mounts which the user has.
     * @param page The index of the page.
     * @return A list which holds all of the mounts which should be displayed on a specific page index.
     */
    public static List<Mount> getMountsForPageIndex(List<Mount> mounts, int page)
    {
        page--;
        //  This page index does start from 0
        int start = 45 * page;
        int stop = start + 45;

        if (start > mounts.size())
            return new ArrayList<>();

        List<Mount> toReturn = new ArrayList<>();

        for(int i = start; i < stop && i < mounts.size(); i++)
            toReturn.add(mounts.get(i));

        return toReturn;
    }

    /**
     * Disabled the GUI interaction so the player can't take, place or swap items.
     * @param event The event when an inventory is clicked.
     */
    @EventHandler
    private void guiMenuClick(InventoryClickEvent event)
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

        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName().replace(" ", "_").toUpperCase());

        player.closeInventory();

        Optional<Rider> riderOptional = Rider.asRider(player);

        if(!riderOptional.isPresent())
            return;

        new Mount(player.getUniqueId(), MountRace.HORSE, 0, "kek").spawn();
    }

}
