package com.github.zastrixarundell.mounts.database;

import com.github.zastrixarundell.mounts.Mounts;
import com.github.zastrixarundell.mounts.entities.Mount;
import com.github.zastrixarundell.mounts.entities.Rider;
import com.github.zastrixarundell.mounts.values.MountRace;

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

    // jmKMwELHdT

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

    public void createMountsTable() throws SQLException
    {
        String query =
                "CREATE TABLE IF NOT EXISTS mounts(" +
                        "owner_uuid VARCHAR(255) NOT NULL," +
                        "race VARCHAR(255) NOT NULL," +
                        "type INTEGER," +
                        "name VARCHAR(255)," +
                        "CONSTRAINT mount_owners " +
                        "FOREIGN KEY (owner_uuid) REFERENCES mounts_players (uuid)" +
                ")";

        Statement statement = connection.createStatement();
        statement.execute(query);
        statement.close();
    }

    // WORK LOGIC

    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static
    {
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

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
                        "SET skill_level = (CASE WHEN skill_level + 0.1 < 10 THEN skill_level + 0.1 ELSE skill_level END)," +
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

        return new Rider(skillLevel, lastDate, getMounts(uuid));
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

    private List<Mount> getMounts(UUID uuid) throws SQLException
    {
        String query =
                "SELECT race, type, name FROM mounts WHERE owner_uuid LIKE \"" + uuid.toString() + "\"";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        List<Mount> mounts = new ArrayList<>();

        while(resultSet.next())
        {
            String raceString = resultSet.getString("race");
            int type = resultSet.getInt("type");
            String name = resultSet.getString("name");

            MountRace race = MountRace.valueOf(raceString.toUpperCase());

            mounts.add(new Mount(uuid, race, type, name));
        }

        return mounts;
    }

    /*
        Function only used on mock database in order to debug.
     */
    public static void main(String[] args) throws SQLException
    {
        String databaseUri = System.getenv("DATABASE_URI");
        MountsDatabase database = new SQLiteDatabase(databaseUri);
        database.createUserTable();
        database.createMountsTable();
        UUID uuid = UUID.fromString("25f7b39e-4458-4b72-9ccb-93efb8213d6a");

        /* Update the level here */
        for (int i = 0; i < 1010; i++)
        {
            Rider rider = database.getPlayerData(uuid);
            System.out.println(rider.getSkillLevel());
            database.updatePlayerLevel(uuid);
            rider = database.getPlayerData(uuid);
            System.out.println(rider.getSkillLevel());
            System.out.println(uuid.toString());
        }
    }

    public void closeConnection() throws SQLException
    {
        connection.close();
    }

}
