package de.doridian.yiffbukkit.mcbans;

import de.doridian.yiffbukkit.database.DatabaseConnectionPool;

import java.lang.ref.SoftReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

public class BanResolver {
    private static HashMap<String, Integer> playerIDs = new HashMap<String, Integer>();
    private static HashMap<Integer, String> playerNames = new HashMap<Integer, String>();
    private static HashMap<Integer, SoftReference<Ban>> playerBans = new HashMap<Integer, SoftReference<Ban>>();

    public static final long BAN_MAX_AGE_MILLIS = 60 * 1000;

    public static Ban getBan(String user) {
        return getBan(user, true);
    }

    public static void addBan(Ban ban) {
        deleteBan(ban);
        try {
            Connection connection = DatabaseConnectionPool.instance.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO bans (reason, admin, player, type, time) VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setString(1, ban.getReason());
            preparedStatement.setInt(2, ban.getAdminID());
            preparedStatement.setInt(3, ban.getUserID());
            preparedStatement.setString(4, ban.getType());
            preparedStatement.setInt(5, ban.getTime());
            preparedStatement.execute();
            preparedStatement.close();
            connection.close();

            playerBans.put(ban.getUserID(), new SoftReference<Ban>(ban));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteBan(Ban ban) {
        try {
			Connection connection = DatabaseConnectionPool.instance.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM bans WHERE player = ?");
            preparedStatement.setInt(1, ban.getUserID());
            preparedStatement.execute();
            preparedStatement.close();
            connection.close();

            playerBans.remove(ban.getUserID());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    protected static Ban getBan(String user, boolean useCaches) {
        int userID = getUserID(user);
        if(playerBans.containsKey(userID)) {
            SoftReference<Ban> cachedBanRef = playerBans.get(userID);
            if(cachedBanRef != null) {
                Ban cachedBan = cachedBanRef.get();
                if(cachedBan != null && ((System.currentTimeMillis() - cachedBan.retrievalTime) < BAN_MAX_AGE_MILLIS)) {
                    return cachedBan;
                } else {
                    playerBans.remove(userID);
                }
            } else {
                playerBans.remove(userID);
            }
        }
        try {
			Connection connection = DatabaseConnectionPool.instance.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bans WHERE player = ?");
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            Ban ret = null;
            if(resultSet.next()) {
                ret = new Ban(resultSet.getString("reason"), resultSet.getInt("admin"), resultSet.getInt("player"), resultSet.getString("type"), resultSet.getInt("time"));
                playerBans.put(userID, new SoftReference<Ban>(ret));
            }
            preparedStatement.close();
            connection.close();
            return ret;
        } catch(Exception e) {
            return new Ban("Database failure", 0, 0, "invalid", 0);
        }
    }

    public static String getUserByID(int id) {
        if(playerNames.containsKey(id)) {
            return playerNames.get(id);
        }
        try {
			Connection connection = DatabaseConnectionPool.instance.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM players WHERE id = ?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            String ret = null;
            if(resultSet.next()) {
                ret = resultSet.getString("name");
                playerIDs.put(ret, id);
                playerNames.put(id, ret);
            }
            preparedStatement.close();
            connection.close();
            return ret;
        } catch(Exception e) {
            return null;
        }
    }

    public static int getUserID(String user) {
        return getUserID(user, false);
    }

    public static int getUserID(String user, boolean create) {
        user = user.toLowerCase();
        if(playerIDs.containsKey(user)) {
            return playerIDs.get(user);
        }
        try {
			Connection connection = DatabaseConnectionPool.instance.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM players WHERE name = ?");
            preparedStatement.setString(1, user);
            ResultSet resultSet = preparedStatement.executeQuery();
            int ret = 0;
            if(resultSet.next()) {
                ret = resultSet.getInt("id");
                playerIDs.put(user, ret);
                playerNames.put(ret, user);
            } else if(create) {
                preparedStatement.close();
                preparedStatement = connection.prepareStatement("INSERT INTO players (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, user);
                ret = preparedStatement.executeUpdate();
                resultSet = preparedStatement.getGeneratedKeys();
                if(resultSet.next()) {
                    ret = resultSet.getInt(1);
                    playerIDs.put(user, ret);
                    playerNames.put(ret, user);
                }
            }
            preparedStatement.close();
            connection.close();
            return ret;
        } catch(Exception e) {
            return 0;
        }
    }
}