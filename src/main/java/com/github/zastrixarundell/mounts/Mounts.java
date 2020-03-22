package com.github.zastrixarundell.mounts;

import com.github.zastrixarundell.mounts.citizens.HostlerTrait;
import com.github.zastrixarundell.mounts.commands.MountsCommand;
import com.github.zastrixarundell.mounts.database.MountsDatabase;
import com.github.zastrixarundell.mounts.database.MySQLDatabase;
import com.github.zastrixarundell.mounts.database.SQLiteDatabase;
import com.github.zastrixarundell.mounts.entities.Mount;
import com.github.zastrixarundell.mounts.gui.HostlerGUI;
import com.github.zastrixarundell.mounts.gui.PlayerGUI;
import com.github.zastrixarundell.mounts.listeners.MountStateListener;
import com.github.zastrixarundell.mounts.listeners.PlayerEventListener;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public class Mounts extends JavaPlugin
{

    private static Mounts plugin;
    private static MountsDatabase database;

    public static final String prefix =
            ChatColor.GRAY + "[" + ChatColor.AQUA + "Mounts" + ChatColor.GRAY + "] " + ChatColor.RESET;

    @Override
    public void onEnable()
    {
        saveDefaultConfig();
        plugin = this;
        new MountsCommand(this);
        new MountStateListener(this);
        new PlayerEventListener(this);
        setupDatabase();
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(HostlerTrait.class).withName("hostler"));
        new HostlerGUI(this);
        new PlayerGUI(this);
    }

    @Override
    public void onDisable()
    {
        for (World world : getServer().getWorlds())
            for (Entity horse : world.getEntitiesByClasses(Horse.class))
                if(Mount.isMount((LivingEntity) horse))
                    horse.remove();

        try
        {
            database.closeConnection();
        }
        catch (Exception ignore)
        {

        }
    }

    private void setupDatabase()
    {
        try
        {
            String hostname = getConfig().getString("hostname");
            String port = getConfig().getString("port");
            String databaseUrl = getConfig().getString("database");
            boolean useSQLite = getConfig().getBoolean("use_sqlite");

            if(!useSQLite && Helpers.isNonEmpty(hostname) && Helpers.isNonEmpty(port)
                    && Helpers.isNonEmpty(databaseUrl))
            {
                String username = getConfig().getString("username");
                String password = getConfig().getString("password");

                database = new MySQLDatabase(username, password, hostname, port, databaseUrl);
            }
            else
                database = new SQLiteDatabase(getDataFolder().getAbsolutePath() + File.separator + "database.db");

            database.createUserTable();
            database.createMountsTable();
        }
        catch (SQLException e)
        {
            getLogger().severe("Error while connecting to the database! Quitting plugin!");
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
    }

    public static Mounts getInstance()
    {
        return plugin;
    }

    public static MountsDatabase getDatabase()
    {
        return database;
    }

}
