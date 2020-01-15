package com.github.zastrixarundell.mounts.values;

import org.bukkit.entity.Horse;

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
}
