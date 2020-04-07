package com.github.zastrixarundell.mounts.database;

import java.sql.*;

public class MySQLDatabase extends MountsDatabase
{

    public MySQLDatabase(String username, String password, String hostname,
                         String port, String database) throws SQLException
    {
        super("jdbc:mysql://" + hostname + ":" + port + "/" + database, username, password);
    }


    /**
     * TODO Need to add this, I am focusing on SQLite for now.
     * @param price
     * @return
     */
    @Override
    String setMountToHostlerQuery(float price)
    {
        return "";
    }
}
