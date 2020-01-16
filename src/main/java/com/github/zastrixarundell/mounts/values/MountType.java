package com.github.zastrixarundell.mounts.values;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum MountType
{

    BRONX_DAPPLE(Horse.Color.CHESTNUT),
    ALDOIR_ASHEN(Horse.Color.GRAY),
    LIRANIAN_BLOND(Horse.Color.WHITE);

    Horse.Color color;

    MountType(Horse.Color color)
    {
        this.color = color;
    }

    public Horse.Color getColor()
    {
        return color;
    }

    public ItemStack asItem()
    {
        String[] parts = name().toLowerCase().split("_");

        for (int i = 0; i < parts.length; i++)
            parts[i] = parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1);

        String name = String.join(" ", parts);

        ItemStack stack = new ItemStack(Material.SADDLE, 1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + name);
        stack.setItemMeta(meta);

        return stack;
    }
}
