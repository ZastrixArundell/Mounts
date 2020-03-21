package com.github.zastrixarundell.mounts.entities;

import com.github.zastrixarundell.mounts.Mounts;
import com.github.zastrixarundell.mounts.database.MountsDatabase;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;

/**
 * Class representing the player in the game. It holds info about the uuid, skill level, last update and
 * all of the mounts which the user has.
 */
public class Rider
{

    // This is a cache of all of the riders in the plugin so there are less
    // calls to the database.
    private static HashMap<UUID, Rider> riderMap = new HashMap<>();

    private UUID user;
    private float skillLevel;
    private String lastDate;
    private List<Mount> mounts;

    /**
     * Constructor for the Rider class. This should generally not be used. And `asRider` should be used more.
     * @param uuid The UUID of the player.
     * @param skillLevel The skill level which the player has.
     * @param lastDate The last date of when the skill was updated.
     * @param mounts List of mounts which the player has.
     */
    public Rider(UUID uuid, float skillLevel, String lastDate, List<Mount> mounts)
    {
        this.user = uuid;
        this.skillLevel = skillLevel;
        this.lastDate = lastDate;
        this.mounts = mounts;

        riderMap.put(uuid, this);
    }

    /**
     * Get the skill level of the player.
     * @return Skill level which the rider has.
     */
    public float getSkillLevel() { return skillLevel; }

    /**
     * Gets the list of mounts which the player has.
     * @return List of mounts which the ruder has.
     */
    public List<Mount> getMounts()
    {
        return mounts;
    }

    /**
     * Gets the last date of when the skill was updated.
     * @return String representation of the last date the skill was updated.
     */
    public String getLastDate()
    {
        return lastDate;
    }

    /**
     * Increments the level of the user by 0.1. Caches and saves to the database at the same time
     */
    public void updateLevel()
    {
        try
        {
            MountsDatabase database = Mounts.getDatabase();
            database.updatePlayerLevel(user);
            skillLevel = (float) Math.min(skillLevel + 0.1, 10);
            riderMap.put(user, this);
        }
        catch (SQLException e)
        {
            Mounts.getInstance().getLogger().warning("Error while updating the skill level of the player " + user.toString() + "!");
        }
    }

    /**
     * Gets the `Rider` corresponding to the player object. Everything regarding this is the same as `asRider(UUID)`.
     * @param player The player object which will be turned into a `Rider` object.
     * @return The `Rider` object.
     */
    public static Optional<Rider> asRider(Player player)
    {
        return asRider(player.getUniqueId());
    }

    /**
     * The best way of getting the data of the user. It will create a new instance and save to the database when it needs to.
     * @param uuid The UUID of the player of whom the Rider object will be created upon.
     * @return Optional containing the `Rider` object
     */
    public static Optional<Rider> asRider(UUID uuid)
    {
        if(riderMap.containsKey(uuid))
            return Optional.of(riderMap.get(uuid));

        try
        {
            MountsDatabase database = Mounts.getDatabase();
            Rider rider = database.getPlayerData(uuid);
            return Optional.of(rider);
        }
        catch (SQLException e)
        {
            Mounts.getInstance().getLogger().severe("Error while reading the player from the database!");
            return Optional.empty();
        }
    }


    /**
     * Deletes the rider bugger/cache. Should generally be handled by the plugin itself.
     * @param player The Player which will be removed from cache.
     */
    public static void deleteRiderBuffer(Player player) { deleteRiderBuffer(player.getUniqueId()); }

    /**
     * Deletes the rider bugger/cache. Should generally be handled by the plugin itself.
     * @param uuid The UUOD of the player which will be removed from cache.
     */
    public static void deleteRiderBuffer(UUID uuid) { riderMap.remove(uuid); }

}
