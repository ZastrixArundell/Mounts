package com.github.zastrixarundell.mounts.database;

import com.github.zastrixarundell.mounts.entities.Rider;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public abstract class MountsDatabase
{

    private Connection connection;

    MountsDatabase(String path) throws SQLException
    {
        connection = DriverManager.getConnection(path);
    }

    public void createUserTable() throws SQLException
    {
        String query =
                "CREATE TABLE IF NOT EXISTS mounts_players(" +
                        "uuid varchar(255) NOT NULL PRIMARY KEY," +
                        "skill_level float NOT NULL DEFAULT 1," +
                        "last_date varchar(255)" +
                ")";

        Statement statement = connection.createStatement();
        statement.execute(query);
        statement.close();
    }

    // jmKMwELHdT

    public void createMountsTable() throws SQLException
    {
        String query =
                "CREATE TABLE IF NOT EXISTS mounts(" +
                        "owner_uuid VARCHAR(255) NOT NULL," +
                        "race VARCHAR(255) NOT NULL," +
                        "type INTEGER," +
                        "name VARCHAR(255)" +
                        "CONSTRAINT mount_owners" +
                        "FOREIGN KEY (owner_uuid) REFERENCES mounts_players (uuid)" +
                ")";

        Statement statement = connection.createStatement();
        statement.execute(query);
        statement.close();
    }

    // WORK LOGIC

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void createPlayerData(UUID uuid) throws SQLException
    {
        String dateNow = simpleDateFormat.format(new Date());

        String command = "INSERT INTO mounts_players(uuid, last_date) " +
                "VALUES (?, ?);";

        PreparedStatement preparedStatement = connection.prepareStatement(command);
        preparedStatement.setString(1, uuid.toString());
        preparedStatement.setString(2, dateNow);

        preparedStatement.execute();
        preparedStatement.close();
    }

    public void updatePlayerLevel(UUID uuid) throws SQLException
    {
        String dateNow = simpleDateFormat.format(new Date());

        String command =
                "UPDATE mounts_players " +
                        "SET skill_level = if(skill_level < 10, skill_level + 0.1, skill_level)," +
                        "last_date=\"" + dateNow + "\" " +
                        "WHERE uuid=\"" + uuid.toString() + "\";";

        Statement statement = connection.createStatement();
        statement.execute(command);
        statement.close();
    }

    public Rider getPlayerData(UUID uuid) throws SQLException
    {
        ResultSet resultSet = getPlayerSQL(uuid);

        float skillLevel = resultSet.getFloat("skill_level");
        String lastDate = resultSet.getString("last_date");
    }

    private ResultSet getPlayerSQL(UUID uuid) throws SQLException
    {
        String query =
                "SELECT skill_level, last_date FROM mounts_players WHERE uuid LIKE \"" + uuid.toString() + "\"";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        if(!resultSet.next())
        {
            createPlayerData(uuid);
            resultSet = getPlayerSQL(uuid);
        }

        return resultSet;
    }

    public int getPlayerId(UUID uuid) throws SQLException
    {
        String query = "SELECT id FROM mounts_players where uuid like \"" + uuid.toString() + "\"";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        if(!resultSet.next())
            return -1;

        int id = resultSet.getInt("id");

        statement.close();

        return id;
    }

}
