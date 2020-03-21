package com.github.zastrixarundell.mounts.database;

import java.sql.SQLException;

public class SQLiteDatabase extends MountsDatabase
{

    public SQLiteDatabase(String uri) throws SQLException
    {
        super("jdbc:sqlite://" + uri);
    }

}
