package com.github.zastrixarundell.mounts.database;

import java.sql.SQLException;

public class SQLiteDatabase extends MountsDatabase
{

    public SQLiteDatabase(String uri) throws SQLException
    {
        super("jdbc:sqlite://" + uri, "", "");
    }


    @Override
    String setMountToHostlerQuery(float price)
    {
        return "INSERT INTO mounts_hostlers(hostler_uuid, mount_id, price) " +
                "VALUES(?, ?, ?) ON CONFLICT(hostler_uuid, mount_id) DO UPDATE SET price = " + price + ";";
    }
}
