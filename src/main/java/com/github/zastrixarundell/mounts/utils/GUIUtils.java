package com.github.zastrixarundell.mounts.utils;

import com.github.zastrixarundell.mounts.Mounts;
import com.github.zastrixarundell.mounts.entities.Mount;
import com.github.zastrixarundell.mounts.entities.Rider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GUIUtils
{

    public static void openGUI(Player player, int page)
    {

        Optional<Rider> riderOptional = Rider.asRider(player);

        if(!riderOptional.isPresent())
            return;

        Rider rider = riderOptional.get();

        List<Mount> mounts = rider.getMounts();
        Inventory inventory = Bukkit.createInventory(player, 54, "Your mounts - page " + page);

        getMountsForPageIndex(mounts, page).forEach(mountType -> inventory.addItem(mountType.getRace().asItem()));

        player.openInventory(inventory);

    }

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

}
