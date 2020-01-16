package com.github.zastrixarundell.mounts.entities;

import com.github.zastrixarundell.mounts.Mounts;
import com.github.zastrixarundell.mounts.values.MountType;
import com.google.gson.*;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Rider
{

    private float speed;
    private List<MountType> knownMounts = new ArrayList<>();

    private Rider()
    {
        float speed = (float) Mounts.getInstance().getConfig().getDouble("default_speed");
        List<String> mounts = Mounts.getInstance().getConfig().getStringList("default_mounts");

        this.speed = speed;

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

    public float getSpeed()
    {
        return speed;
    }

    public List<MountType> getKnownMounts()
    {
        return knownMounts;
    }

    public static Rider asRider(Player player)
    {
        UUID uuid = player.getUniqueId();

        File dataFolder = Mounts.getInstance().getDataFolder();
        File playerFolder = new File(dataFolder.getPath() + File.separator + "players");

        if(!playerFolder.exists())
            playerFolder.mkdirs();

        File userFile = new File(playerFolder.getPath() + File.separator + uuid.toString() + ".json");

        try
        {
            return userFile.exists() ? deserializeJSON(userFile) : createRider(userFile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new Rider();
        }
    }

    private static Rider deserializeJSON(File file) throws IOException
    {
        JsonParser parser = new JsonParser();

        JsonObject object = (JsonObject) parser.parse(new FileReader(file));

        Rider rider = new Rider();
        rider.speed = object.get("speed").getAsFloat();

        JsonArray array = object.getAsJsonArray("mounts");

        for (JsonElement jsonElement : array)
            try
            {
                String name = jsonElement.getAsString();
                rider.knownMounts.add(MountType.valueOf(name));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        return rider;
    }

    private static Rider createRider(File file)
    {
        Rider rider = new Rider();

        Runnable runnable = () ->
        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("speed", rider.speed);

            JsonArray mountArray = new JsonArray();
            rider.knownMounts.forEach(mountType -> mountArray.add(mountType.name()));

            jsonObject.add("mounts", mountArray);

            String json = jsonObject.toString();

            try
            {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));

                writer.write(json);

                writer.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        };

        new Thread(runnable).start();

        return rider;
    }

}
