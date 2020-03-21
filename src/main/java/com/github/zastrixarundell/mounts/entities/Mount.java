package com.github.zastrixarundell.mounts.entities;

import com.github.zastrixarundell.mounts.Mounts;
import com.github.zastrixarundell.mounts.values.MountRace;
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

import java.util.Optional;
import java.util.UUID;

public class Mount
{

    private static final Horse.Color[] horseColors =
            {
                Horse.Color.WHITE,
                Horse.Color.CREAMY,
                Horse.Color.CHESTNUT,
                Horse.Color.BROWN,
                Horse.Color.BLACK,
                Horse.Color.GRAY,
                Horse.Color.GRAY,
                Horse.Color.DARK_BROWN
            };

    private static final Horse.Style[] horseStyles =
            {
                Horse.Style.NONE,
                Horse.Style.WHITE,
                Horse.Style.WHITEFIELD,
                Horse.Style.WHITE_DOTS,
                Horse.Style.BLACK_DOTS
            };

    private UUID owner;
    private MountRace race;
    private int type;
    private String name;

    public Mount(UUID owner, MountRace race, int type, String name)
    {
        this.owner = owner;
        this.race = race;
        this.type = type;
        this.name = name;
    }

    public MountRace getRace()
    {
        return race;
    }

    public void spawn()
    {
        Player player = Bukkit.getPlayer(owner);

        if (player == null)
            return;

        Optional<Rider> riderOptional = Rider.asRider(player);

        if(!riderOptional.isPresent())
            return;

        Rider rider = riderOptional.get();

        Location toSpawnLocation = player.getLocation().clone();

        if (toSpawnLocation.getWorld() == null)
            return;

        Horse horseEntity = (Horse) toSpawnLocation.getWorld().spawnEntity(toSpawnLocation, EntityType.HORSE);

        AttributeInstance attribute = horseEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);

        if (attribute == null)
            return;

        attribute.setBaseValue(0.21f + rider.getSkillLevel() / 100f);

        horseEntity.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
        horseEntity.setTamed(true);

        horseEntity.setOwner(player);
        horseEntity.setPassenger(player);

        horseEntity.setMetadata("custom_mount", new FixedMetadataValue(Mounts.getInstance(), true));
    }

    public static boolean isMount(LivingEntity entity) { return entity.hasMetadata("custom_mount"); }

}
