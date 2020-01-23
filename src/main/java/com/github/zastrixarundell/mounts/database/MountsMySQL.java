package com.github.zastrixarundell.mounts.database;

import com.github.zastrixarundell.mounts.Mounts;
import com.github.zastrixarundell.mounts.entities.Rider;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

public class MountsMySQL
{

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String hostname, username, password, database, port;

    private Connection connection;

    public MountsMySQL setHostname(String hostname)
    {
        this.hostname = hostname;
        return this;
    }

    public MountsMySQL setUsername(String username)
    {
        this.username = username;
        return this;
    }

    public MountsMySQL setPassword(String password)
    {
        this.password = password;
        return this;
    }

    public MountsMySQL setDatabase(String database)
    {
        this.database = database;
        return this;
    }

    public MountsMySQL setPort(String port)
    {
        this.port = port;
        return this;
    }

    public void openConnection() throws SQLException
    {
        connection = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port +
                "/" + database, username, password);

    }

    public void createUserTable() throws SQLException
    {
        String query =
                "CREATE TABLE IF NOT EXISTS mounts_players(" +
                "   id int NOT NULL AUTO_INCREMENT," +
                "   uuid varchar(36) NOT NULL," +
                "   skill_level float NOT NULL DEFAULT 1," +
                "   last_date varchar(19)," +
                "   PRIMARY KEY (id)" +
                ")";

        Statement statement = connection.createStatement();
        statement.execute(query);
        statement.close();
    }

    public void createOwnersTable() throws SQLException
    {
        String query =
                "CREATE TABLE IF NOT EXISTS mounts_owners(" +
                "    owner INTEGER NOT NULL,\n" +
                "    mount VARCHAR(255) NOT NULL\n" +
                ")";

        Statement statement = connection.createStatement();
        statement.execute(query);
        statement.close();
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

        List<String> mounts = Mounts.getInstance().getConfig().getStringList("default_mounts");

        int id = getPlayerId(uuid);
        mounts.forEach(mount -> {
            try
            {
                addOwner(id, mount);
            }
            catch (SQLException ignore)
            {

            }
        });
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

    public void addOwner(int id, String mountName) throws SQLException
    {
        String command =
                "INSERT INTO mounts_owners(owner, mount) VALUES (?, ?);";

        PreparedStatement preparedStatement = connection.prepareStatement(command);
        preparedStatement.setInt(1, id);
        preparedStatement.setString(2, mountName);

        preparedStatement.execute();
        preparedStatement.close();
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

    public Optional<Rider> getPlayerData(UUID uuid) throws SQLException
    {
        String query = "SELECT * FROM mounts_players where uuid like \"" + uuid.toString() + "\"";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        if(!resultSet.next())
            return Optional.empty();

        int id = resultSet.getInt("id");
        float skillLevel = resultSet.getFloat("skill_level");
        String lastDate = resultSet.getString("last_date");

        statement.close();

        query = "SELECT mount FROM mounts_owners WHERE owner = " + id;

        statement = connection.createStatement();
        resultSet = statement.executeQuery(query);

        List<String> mountCollection = new ArrayList<>();

        while (resultSet.next())
            mountCollection.add(resultSet.getString("mount"));

        statement.close();

        Rider rider = new Rider(skillLevel, id, lastDate, mountCollection);

        return Optional.of(rider);
    }
}
