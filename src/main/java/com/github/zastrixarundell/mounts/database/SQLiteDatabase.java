package com.github.zastrixarundell.mounts.database;

import com.github.zastrixarundell.mounts.Mounts;

import java.io.File;
import java.sql.SQLException;

public class SQLiteDatabase extends MountsDatabase
{

    public SQLiteDatabase() throws SQLException
    {
        super(generateDatabasePath());
    }

    public static String generateDatabasePath()
    {
        return Mounts.getInstance().getDataFolder() + File.separator + "database.db";
    }

}
