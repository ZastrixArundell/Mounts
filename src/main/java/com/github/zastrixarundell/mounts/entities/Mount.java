package com.github.zastrixarundell.mounts.entities;

import com.github.zastrixarundell.mounts.Mounts;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

public class Mount
{

    private Horse horseEntity;
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

        horseEntity = (Horse) toSpawnLocation.getWorld().spawnEntity(toSpawnLocation, EntityType.HORSE);

        horseEntity.setOwner(player);

        AttributeInstance attribute = horseEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);

        if (attribute == null)
        {
            player.sendMessage("This doesn't work, FUUUUUCK!");
            return;
        }

        attribute.setBaseValue(speed);

        horseEntity.setTamed(true);

        horseEntity.setPassenger(player);

        horseEntity.setMetadata("custommount", new FixedMetadataValue(Mounts.getInstance(), owner));
    }

    public void removeEntity()
    {
        horseEntity.remove();
    }

}
