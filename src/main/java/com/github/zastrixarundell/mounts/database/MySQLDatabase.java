package com.github.zastrixarundell.mounts.database;

import java.sql.*;

public class MySQLDatabase extends MountsDatabase
{

    public MySQLDatabase(String username, String password, String hostname,
                         String port, String database) throws SQLException
    {
        super("jdbc:mysql://" + username + ":" + password + "@" +
                hostname + ":" + port + "/" + database);
    }

}
