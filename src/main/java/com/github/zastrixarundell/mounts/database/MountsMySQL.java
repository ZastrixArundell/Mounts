package com.github.zastrixarundell.mounts.database;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

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

    public int getPlayerData(UUID uuid) throws SQLException
    {
        String query = "SELECT * FROM mounts_players where uuid like \"" + uuid.toString() + "\"";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        resultSet.next();

        int id = resultSet.getInt("id");
        float skillLevel = resultSet.getFloat("skill_level");
        String lastDate = resultSet.getString("last_date");

        statement.close();

        /*
            query = "SELECT mount FROM mounts_player_mounts WHERE uuid LIKE \"" + uuid.toString() + "\"";

            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            Collection<String> mountCollection = new ArrayList<>();

            while (resultSet.next())
                mountCollection.add(resultSet.getString("mount"));

            statement.close();

            System.out.println("UUID: " + uuid.toString());
            System.out.println("ID: " + id);
            System.out.println("Level: " + skillLevel);
            System.out.println("Last_Date: " + lastDate);
            System.out.println("Mounts: ");
            mountCollection.forEach(mount -> System.out.println("  - " + mount));
        */

        return id;
    }

    public static void main(String[] args) throws SQLException
    {
        MountsMySQL sql = new MountsMySQL()
                .setHostname(System.getenv("SQL_HOSTNAME"))
                .setPort(System.getenv("SQL_PORT"))
                .setDatabase(System.getenv("SQL_DATABASE"))
                .setUsername(System.getenv("SQL_USERNAME"))
                .setPassword(System.getenv("SQL_PASSWORD"));

        sql.openConnection();
        sql.createUserTable();
        sql.createOwnersTable();

        UUID uuid = UUID.randomUUID();

        sql.createPlayerData(uuid);

        int id = sql.getPlayerData(uuid);

        for (int i = 0; i < 5; i++)
            sql.addOwner(id, "mount_" + i);

        for (int i = 0; i < 10; i++)
            sql.updatePlayerLevel(uuid);
    }
}
