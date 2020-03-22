package com.github.zastrixarundell.mounts.gui;

import com.github.zastrixarundell.mounts.Mounts;
import com.github.zastrixarundell.mounts.database.MountsDatabase;
import com.github.zastrixarundell.mounts.entities.Rider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * General class corresponding to the GUI of which Hostlers open to the player.
 * It will as well catch every event which corresponds to it.
 */
public class HostlerGUI implements Listener
{

    public HostlerGUI(Mounts plugin)
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Open the GUI to the player in question.
     *
     * TODO Add functionality to sell mounts to players via this.
     * @param player The player in question to whom the GUI will be opened.
     * @param page The index of the page which should be opened.
     */
    public static void openHostlerGUIToPlayer(Player player, int page)
    {
        Optional<Rider> riderOptional = Rider.asRider(player);

        if(!riderOptional.isPresent())
            return;

        Rider rider = riderOptional.get();

        Date currentInGMT = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();
        Date lastUpdate;

        try
        {
           lastUpdate = MountsDatabase.simpleDateFormat.parse(rider.getLastDate());
        }
        catch (Exception e)
        {
            Mounts.getInstance().getLogger().severe("Error while parsing last time for " + rider.getUUID() + "!");
            return;
        }

        long difference = currentInGMT.getTime() - lastUpdate.getTime();
        long inDays = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);

        ItemStack button = inDays >= 1 ? getOkayItemStack() : getNonOkayItemStack(difference);

        Inventory inventory = Bukkit.createInventory(player, 54, "Hostler - page " + page);
        inventory.setItem(53, button);

        player.openInventory(inventory);
    }

    /**
     * Gets the item stack which is okay for the user to level up.
     * @return The ItemStack which is a button for leveling up.
     */
    private static ItemStack getOkayItemStack()
    {
        ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
        ItemMeta meta = item.getItemMeta();

        if(meta == null)
            return item;

        meta.setDisplayName(ChatColor.GREEN + "Level riding skill!");
        meta.setLore(Arrays.asList("Level up the riding skill of the player.", " ", "Can be done every 24 hours!"));

        item.setItemMeta(meta);

        return item;
    }

    /**
     * Gets the item stack for which it isn't okay for the user to level up.
     * @param difference Difference in time between now and the last time the user was updated.
     * @return The ItemStack which is a button to not level up.
     */
    private static ItemStack getNonOkayItemStack(long difference)
    {
        difference = TimeUnit.DAYS.toMillis(1) - difference;

        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
        ItemMeta meta = item.getItemMeta();

        if(meta == null)
            return item;

        meta.setDisplayName(ChatColor.RED + "Need to wait to level riding skill!");

        long hours = TimeUnit.HOURS.convert(difference, TimeUnit.MILLISECONDS);
        long minutes = TimeUnit.MINUTES.convert(difference, TimeUnit.MILLISECONDS) - hours*60;
        long seconds = TimeUnit.SECONDS.convert(difference, TimeUnit.MILLISECONDS) - hours*60*60 - minutes*60;

        meta.setLore(Arrays.asList("Level up the riding skill of the player.", " ", "You need to wait " +
                hours + " hour(s), " + minutes + " minute(s) and " + seconds + " second(s)."));

        item.setItemMeta(meta);

        return item;
    }

    /**
     * Event handler which catches all events for the HostlerGUI.
     * @param event The InventoryClickEvent.
     */
    @EventHandler
    private void guiMenuClick(InventoryClickEvent event)
    {
        if(!event.getView().getTitle().startsWith("Hostler - page"))
            return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        player.updateInventory();

        if (event.getCurrentItem() == null)
            return;

        if (event.getCurrentItem().getItemMeta() == null)
            return;

        ItemStack item = event.getCurrentItem();

        if(item.isSimilar(getOkayItemStack()))
        {
            Optional<Rider> riderOptional = Rider.asRider(player);

            if(!riderOptional.isPresent())
                return;

            riderOptional.get().updateLevel();

            player.closeInventory();
        }
    }

}
