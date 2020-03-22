package com.github.zastrixarundell.mounts.database;

import com.github.zastrixarundell.mounts.entities.Mount;
import com.github.zastrixarundell.mounts.entities.Rider;
import com.github.zastrixarundell.mounts.values.MountRace;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Abstracted class which works for general SQL. It might be altered
 * so there is an interface for general DB IO so more database types are
 * supported.
 */
public abstract class MountsDatabase
{

    private Connection connection;

    MountsDatabase(String path, String username, String password) throws SQLException
    {
        connection = DriverManager.getConnection(path, username, password);
    }

    /*

        Table creation data

     */

    /**
     * Creates the table which corresponds to the owners of this plugin. Relation is one to many
     * (one owner has many mounts).
     * @throws SQLException Exception thrown in case there is an error while creating the table.
     */
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

    /**
     * Create the table which corresponds to the mount owning table. Relation of this is many to many.
     * @throws SQLException Exception thrown in case there is an error while creating the table.
     */
    public void createMountOwningTable() throws SQLException
    {
        String query =
                "CREATE TABLE IF NOT EXISTS mounts_owning(" +
                        "owner_uuid VARCHAR(255) NOT NULL," +
                        "mount_id INTEGER NOT NULL," +
                        "FOREIGN KEY (owner_uuid) REFERENCES mounts_players (uuid)," +
                        "FOREIGN KEY (mount_id) REFERENCES mounts_list (id)" +
                ")";

        Statement statement = connection.createStatement();
        statement.execute(query);
        statement.close();
    }

    /**
     * Create the table corresponding to all of the available mounts on the server. Relation is
     * many to many with the owning table.
     * @throws SQLException Exception thrown in case there is an error while creating the table.
     */
    public void createMountsTable() throws SQLException
    {
        String query =
                "CREATE TABLE IF NOT EXISTS mounts_list(" +
                        "id INTEGER NOT NULL PRIMARY KEY," +
                        "race VARCHAR(255) NOT NULL," +
                        "type INTEGER," +
                        "name VARCHAR(255)" +
                ")";

        Statement statement = connection.createStatement();
        statement.execute(query);
        statement.close();
    }

    /**
     * Hostler table from which you can buy mounts. It has the data for the uuid of the hostler,
     * the mount id and the price of the mount for that specific hostler.
     * @throws SQLException Exception in csse there is an error
     */
    public void createHolstersTable() throws SQLException
    {
        String query =
                "CREATE TABLE IF NOT EXISTS mounts_hostlers(" +
                        "hostler_uuid VARCHAR(255) NOT NULL," +
                        "mount_id INTEGER NOT NULL," +
                        "price DOUBLE DEFAULT 0," +
                        "FOREIGN KEY (mount_id) REFERENCES mounts_list (id)," +
                        "CONSTRAINT unique_hostler_mount UNIQUE (hostler_uuid, mount_id)" +
                ")";

        Statement statement = connection.createStatement();
        statement.execute(query);
        statement.close();
    }

    /*

        Work logic of the database IO starts here.

     */





    /*
         Simple date format which will be written and read from the database.
         The static block after this just sets the timezone as setting the timezone is a void function.
     */
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static
    {
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /*

        All of the functions regarding players-specific data.

     */

    /**
     * Gets the data of the player corresponding to the specified UUID. Returns an
     * `Rider` object which contains all of the necessary data. This is one of the general
     * methods to be used in this class.
     * @param uuid The UUID of the player.
     * @return Rider object of the player.
     * @throws SQLException Exception in case an error happened during IO.
     */
    public Rider getPlayerData(UUID uuid) throws SQLException
    {
        ResultSet resultSet = getPlayerSQL(uuid);

        float skillLevel = resultSet.getFloat("skill_level");
        String lastDate = resultSet.getString("last_date");

        return new Rider(uuid, skillLevel, lastDate, getMounts(uuid));
    }

    /**
     * Updates the level of the player. It also updates the last time the player was updated.
     * @param uuid The UUID of the player.
     * @throws SQLException Exception in case an error happened during IO.
     */
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

    /**
     * Gets the `ResultSet` which corresponds to the player data. Shouldn't be used outside of class.
     * @param uuid The UUID of the player.
     * @return The `ResultSet` containing the player data.
     * @throws SQLException Exception in case an error happened during IO.
     */
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

    /**
     * This creates the player data in the database. Doesn't need to be called from
     * other places outside of the class instance. It is automatically ran in:
     * `getPlayerSQL(UUID`.
     * @param uuid The UUID of the player from where the data needs to be taken.
     * @throws SQLException Exception in case there is an error during IO.
     */
    private void createPlayerData(UUID uuid) throws SQLException
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

    /*

        Data which is half player half mount

     */

    /**
     * Gives a mount to the player.
     * @param playerUUID The UUID of the player.
     * @param mountId The ID of the mount.
     * @throws SQLException Exception in case an error happened during IO.
     */
    public void giveMountToPlayer(UUID playerUUID, int mountId) throws SQLException
    {
        String command = "INSERT INTO mounts_owning(owner_uuid, mount_id) " +
                "VALUES (?, ?);";

        PreparedStatement preparedStatement = connection.prepareStatement(command);
        preparedStatement.setString(1, playerUUID.toString());
        preparedStatement.setInt(2, mountId);

        preparedStatement.execute();
        preparedStatement.close();
    }

    /**
     * Gets the mounts of the players. This is a low level function as it
     * directly gets the data for the player. Rather than using this you should
     * get the `Rider` object of the player and then get the mount data off from there.
     * @param uuid The UUID of the player.
     * @return A list of mounts which the player has.
     * @throws SQLException Exception in case there is an error during IO.
     */
    private List<Mount> getMounts(UUID uuid) throws SQLException
    {
        String query =
                "SELECT mount_id FROM mounts_owning WHERE owner_uuid LIKE \"" + uuid.toString() + "\"";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        ArrayList<String> ids = new ArrayList<>();

        while(resultSet.next())
            ids.add(String.valueOf(resultSet.getInt("mount_id")));

        String names = String.join(", ", ids);

        query =
                "SELECT race, type, name FROM mounts_list WHERE id IN (" + names + ")";

        statement = connection.createStatement();
        resultSet = statement.executeQuery(query);

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

        Data which is mount specific

     */

    /**
     * Inserts a mount to the `mount_list` table. ID is automatically incremented.
     * @param race The race of the mount.
     * @param type The type of the mount.
     * @param name The name of the mount.
     * @throws SQLException Exception in case there is an error during IO.
     */
    public void insertNewMount(String race, int type, String name) throws SQLException
    {
        String query =
                "SELECT COUNT(id) AS count FROM mounts_list";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        resultSet.next();

        int count = resultSet.getInt("count");
        count++;

        String command =
                "INSERT INTO mounts_list(id, race, type, name) " +
                "VALUES (?, ?, ?, ?);";

        PreparedStatement preparedStatement = connection.prepareStatement(command);
        preparedStatement.setInt(1, count);
        preparedStatement.setString(2, race);
        preparedStatement.setInt(3, type);
        preparedStatement.setString(4, name);

        preparedStatement.execute();
        preparedStatement.close();
    }

    /*

        Data which is hostler specific

     */

    abstract String setMountToHostlerQuery(float price);

    /**
     * Sets the mount to the hostler. Add or update when needed.
     * @param hostlerUUID The UUID of the hostler.
     * @param mountId The ID of the mount.
     * @param price The price of the mount.
     * @throws SQLException Exception thrown in case there is an error during IO.
     */
    public void setMountToHostler(UUID hostlerUUID, int mountId, float price) throws SQLException
    {
        String query = setMountToHostlerQuery(price);

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, hostlerUUID.toString());
        preparedStatement.setInt(2, mountId);
        preparedStatement.setFloat(3, price);

        preparedStatement.execute();
        preparedStatement.close();
    }

    /**
     * Removed the mount from the hostler.
     * @param hostlerUUID The UUID of the hostler.
     * @param mountId The ID of the mount.
     * @throws SQLException Exception thrown in case there is an error during IO.
     */
    public void removeMountFromHostler(UUID hostlerUUID, int mountId) throws SQLException
    {
        String query =
                "DELETE FROM mounts_hostlers WHERE mount_id = " + mountId;

        Statement statement = connection.createStatement();
        statement.execute(query);
        statement.close();
    }

    /**
     * Function only used when debugging the database. Unnecessary when used as a plugin.
     * @param args Args so it is compatible with Java main, unneeded.
     * @throws SQLException Exception in cases there in an error during IO.
     */
    public static void main(String[] args) throws SQLException
    {
        String databaseUri = System.getenv("DATABASE_URI");
        MountsDatabase database = new SQLiteDatabase(databaseUri);
        database.createUserTable();
        database.createMountsTable();
        database.createMountOwningTable();
        database.createHolstersTable();

        UUID uuid = UUID.fromString("25f7b39e-4458-4b72-9ccb-93efb8213d6a");

        /*
        // Update the level here
        for (int i = 0; i < 1010; i++)
        {
            Rider rider = database.getPlayerData(uuid);
            System.out.println(rider.getSkillLevel());
            database.updatePlayerLevel(uuid);
            rider = database.getPlayerData(uuid);
            System.out.println(rider.getSkillLevel());
            System.out.println(uuid.toString());
        }*/

        database.setMountToHostler(uuid, 3, 100);
        database.setMountToHostler(uuid, 3, 101);
        database.setMountToHostler(uuid, 3, 110);
        database.setMountToHostler(uuid, 3, 0);

        database.insertNewMount("Horse", 1, "Ello");
    }

    /**
     * Close the connection to the database to prevent db lock.
     * @throws SQLException Exception if the connection wasn't closed.
     */
    public void closeConnection() throws SQLException
    {
        connection.close();
    }

}
