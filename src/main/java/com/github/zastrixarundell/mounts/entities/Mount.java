package com.github.zastrixarundell.mounts.entities;

import com.github.zastrixarundell.mounts.Mounts;
import com.github.zastrixarundell.mounts.values.MountType;
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
    private MountType type;

    public Mount(Player owner, float speed, MountType type)
    {
        this.owner = owner.getUniqueId();
        this.speed = speed;
        this.type = type;
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

        horseEntity.setColor(type.getColor());

        AttributeInstance attribute = horseEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);

        if (attribute == null)
            return;

        attribute.setBaseValue(speed);

        horseEntity.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
        horseEntity.setTamed(true);

        horseEntity.setOwner(player);
        horseEntity.setPassenger(player);

        horseEntity.setMetadata("custom_mount", new FixedMetadataValue(Mounts.getInstance(), true));
    }

    public static boolean isMount(LivingEntity entity) { return entity.hasMetadata("custom_mount"); }

}
