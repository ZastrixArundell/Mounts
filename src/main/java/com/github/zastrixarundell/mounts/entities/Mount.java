package com.github.zastrixarundell.mounts.entities;

import com.github.zastrixarundell.mounts.Mounts;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

public class Mount
{

    private float speed;
    private UUID owner;

    public Mount(Player owner, float speed)
    {
        this.owner = owner.getUniqueId();
        this.speed = speed;
    }

    public void spawn()
    {
        Player player = Bukkit.getPlayer(owner);

        if (player == null)
            return;

        Location toSpawnLocation = player.getLocation().clone();

        if (toSpawnLocation.getWorld() == null)
            return;

        Horse horseEntity = (Horse) toSpawnLocation.getWorld().spawnEntity(toSpawnLocation, EntityType.HORSE);

        horseEntity.setOwner(player);

        AttributeInstance attribute = horseEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);

        if (attribute == null)
        {
            player.sendMessage("This doesn't work, FUUUUUCK!");
            return;
        }

        horseEntity.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));

        attribute.setBaseValue(speed);

        horseEntity.setTamed(true);

        horseEntity.setPassenger(player);

        horseEntity.setMetadata("custom_mount", new FixedMetadataValue(Mounts.getInstance(), true));
    }

    public static boolean isMount(LivingEntity entity)
    {
        return entity.hasMetadata("custom_mount");
    }

}
