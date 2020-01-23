package com.github.zastrixarundell.mounts.entities;

import com.github.zastrixarundell.mounts.Mounts;
import com.github.zastrixarundell.mounts.values.MountType;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;

public class Rider
{

    private static HashMap<UUID, Rider> riderMap = new HashMap<>();

    private float skillLevel;
    private int id;
    private String lastDate;

    private List<MountType> knownMounts = new ArrayList<>();

    public Rider(float skillLevel, int id, String lastDate, List<String> mounts)
    {
        this.skillLevel = skillLevel;
        this.id = id;
        this.lastDate = lastDate;

        for (String mountName : mounts)
            try
            {
                knownMounts.add(MountType.valueOf(mountName));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
    }

    public float getSkillLevel() { return skillLevel; }

    public int getId() { return id; }

    public List<MountType> getKnownMounts()
    {
        return knownMounts;
    }

    public static Optional<Rider> asRider(Player player)
    {
        UUID uuid = player.getUniqueId();

        if(riderMap.containsKey(uuid))
            return Optional.of(riderMap.get(uuid));

        try
        {
            Optional<Rider> riderOptional = Mounts.getMySQL().getPlayerData(uuid);

            if (!riderOptional.isPresent())
            {
                Mounts.getMySQL().createPlayerData(uuid);
                riderOptional = Mounts.getMySQL().getPlayerData(uuid);
            }

            return riderOptional;
        }
        catch (SQLException e)
        {
            return Optional.empty();
        }
    }


    public static void deleteRiderBuffer(Player player) { riderMap.remove(player.getUniqueId()); }

}
