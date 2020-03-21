package com.github.zastrixarundell.mounts.values;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum MountRace
{

    MULE,
    DONKEY,
    HORSE,
    PIG;

    public ItemStack asItem()
    {
        String name = name().substring(0, 1).toUpperCase() + name().substring(1);

        ItemStack stack = new ItemStack(Material.SADDLE, 1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + name);
        stack.setItemMeta(meta);

        return stack;
    }
}
