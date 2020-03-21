package com.github.zastrixarundell.mounts.entities;

import com.github.zastrixarundell.mounts.Mounts;
import com.github.zastrixarundell.mounts.database.MountsDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;

public class Rider
{

    private static HashMap<UUID, Rider> riderMap = new HashMap<>();

    private float skillLevel;
    private String lastDate;
    private List<Mount> mounts;

    public Rider(float skillLevel, String lastDate, List<Mount> mounts)
    {
        this.skillLevel = skillLevel;
        this.lastDate = lastDate;
        this.mounts = mounts;
    }

    public float getSkillLevel() { return skillLevel; }

    public List<Mount> getMounts()
    {
        return mounts;
    }

    public String getLastDate()
    {
        return lastDate;
    }

    public static Optional<Rider> asRider(Player player)
    {
        UUID uuid = player.getUniqueId();

        if(riderMap.containsKey(uuid))
            return Optional.of(riderMap.get(uuid));

        try
        {
            MountsDatabase database = Mounts.getDatabase();
            Rider rider = database.getPlayerData(uuid);
            Mounts.getInstance().getLogger().info(String.valueOf(rider.getMounts().size()));
            return Optional.of(rider);
        }
        catch (SQLException e)
        {
            Mounts.getInstance().getLogger().severe("Error while reading the player from the database!");
            return Optional.empty();
        }
    }


    public static void deleteRiderBuffer(Player player) { riderMap.remove(player.getUniqueId()); }

}
