package xyz.minealex.townybattle.db;

import xyz.minealex.townybattle.TownyBattle;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DB
{
    private Connection townsDBConn;
    private Connection jailedDBConn;

    private List<Battle> battleList = new ArrayList<>();
    private List<UUID> jailedList = new ArrayList<>();

    private static DB instance;

    public static DB getInstance()
    {
        if (instance == null) instance = new DB();

        return instance;
    }

    private DB()
    {
        try
        {
            new File("./plugins/TownyBattle/db").mkdirs();

            Class.forName("org.sqlite.JDBC");

            townsDBConn = DriverManager.getConnection("jdbc:sqlite:./plugins/TownyBattle/db/towns.db");
            jailedDBConn = DriverManager.getConnection("jdbc:sqlite:./plugins/TownyBattle/db/jailed.db");

            createTables();
            update();
        }
        catch (ClassNotFoundException | SQLException e) { TownyBattle.getInstance().disable(e, "init critical error"); }
    }

    public void closeConnection()
    {
        try
        {
            clearTables();
            loadDataToBD();

            if (townsDBConn != null) townsDBConn.close();
            if (jailedDBConn != null) jailedDBConn.close();
        }
        catch (SQLException reason) { TownyBattle.getInstance().printWarn(reason, "SQLite disconnection error"); }
    }

    public List<Battle> getBattleList() { return battleList; }
    public List<UUID> getJailedList() { return jailedList; }

    public Battle getBattleByName(String battleName)
    {
        Battle battle = null;

        for (Battle thisBattle: DB.getInstance().getBattleList())
            if (thisBattle.getBattleName().equalsIgnoreCase(battleName))
            {
                battle = thisBattle;
                break;
            }

        return battle;
    }

    private void update()
    {
        jailedList = getJailedFromDB();
        battleList = getBattleFromDB();
    }

    private void loadDataToBD()
    {
        for (Battle battle : battleList)
            addBattleDB(battle.getBattleName(), battle.getTowns(), battle.isActive());
        setJailedDB();
    }

    private void createTables()
    {
        PreparedStatement townStatement = null;
        PreparedStatement jailedStatement = null;

        try
        {
            townStatement = townsDBConn.prepareStatement("CREATE TABLE if not exists `townsTable` (" +
                                                             "`battleName` TEXT NOT NULL," +
                                                             "`towns` TEXT NOT NULL," +
                                                             "`isActive` CHAR(1) NOT NULL" +
                                                             ")");
            jailedStatement = jailedDBConn.prepareStatement("CREATE TABLE if not exists `jailedTable` (`jailed` TEXT NOT NULL )");

            townStatement.execute();
            jailedStatement.execute();
        }
        catch (SQLException e) { TownyBattle.getInstance().disable(e, "createTables critical error"); }
        finally
        {
            try
            {
                if (townStatement != null) townStatement.close();
                if (jailedStatement != null) jailedStatement.close();
            }
            catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void clearTables()
    {
        PreparedStatement townStatement = null;
        PreparedStatement jailedStatement = null;

        try
        {
            townStatement = townsDBConn.prepareStatement("DELETE FROM `townsTable`");
            jailedStatement = jailedDBConn.prepareStatement("DELETE FROM `jailedTable`");

            townStatement.executeUpdate();
            jailedStatement.executeUpdate();
        }
        catch (SQLException e) { TownyBattle.getInstance().disable(e, "clearTables critical error"); }
        finally
        {
            try
            {
                if (townStatement != null) townStatement.close();
                if (jailedStatement != null) jailedStatement.close();
            }
            catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private List<Battle> getBattleFromDB()
    {
        List<Battle> battleListDB = new ArrayList<>();

        PreparedStatement townStatement = null;
        ResultSet resultSet = null;

        try
        {
            townStatement = townsDBConn.prepareStatement("SELECT * FROM `townsTable`");

            resultSet = townStatement.executeQuery();

            while (resultSet.next())
            {
                String battleName = resultSet.getString("battleName");
                String townsString = resultSet.getString("towns");
                boolean isActive = resultSet.getString("isActive").equals("Y");
                List<UUID> townList = new ArrayList<>();

                if (townsString.length() != 0)
                    for (String strUUID : townsString.substring(0, townsString.length() - 1).split(" "))
                        townList.add(UUID.fromString(strUUID));

                battleListDB.add(new Battle(battleName, townList, isActive));
            }
        }
        catch (SQLException e) { TownyBattle.getInstance().printWarn(e, "getTowns error"); }
        finally
        {
            try
            {
                if (townStatement != null) townStatement.close();
                if (resultSet != null) resultSet.close();
            }
            catch (SQLException e) { e.printStackTrace(); }
        }

        return battleListDB;
    }

    private List<UUID> getJailedFromDB()
    {
        List<UUID> jailedListDB = new ArrayList<>();

        PreparedStatement jailedStatement = null;
        ResultSet resultSet = null;

        try
        {
            jailedStatement = jailedDBConn.prepareStatement("SELECT * FROM `jailedTable`");

            resultSet = jailedStatement.executeQuery();
            resultSet.next();

            if (resultSet.next())
            {
                String jailedString = resultSet.getString("jailed");

                for (String strUUID : jailedString.substring(0, jailedString.length() - 1).split(" "))
                    jailedListDB.add(UUID.fromString(strUUID));
            }
        }
        catch (SQLException e) { TownyBattle.getInstance().printWarn(e, "getJailed error"); }
        finally
        {
            try
            {
                if (jailedStatement != null) jailedStatement.close();
                if (resultSet != null) resultSet.close();
            }
            catch (SQLException e) { e.printStackTrace(); }
        }

        return jailedListDB;
    }

    private void addBattleDB(String battleName, List<UUID> townList, boolean isActive)
    {
        PreparedStatement townStatement = null;

        String townsString = "";

        for (UUID UUID : townList)
            townsString += UUID + " ";

        String isActiveStr = "Y";
        if (!isActive) isActiveStr = "N";

        try
        {
            townStatement = townsDBConn.prepareStatement("INSERT INTO `townsTable` " +
                                                             "(`battleName`, `towns`, `isActive`) " +
                                                             "VALUES " +
                                                             "(?, ?, ?)");
                townStatement.setString(1, battleName);
                townStatement.setString(2, townsString);
                townStatement.setString(3, isActiveStr);

            townStatement.executeUpdate();
        }
        catch (SQLException e) { TownyBattle.getInstance().printWarn(e, "addBattle error"); }
        finally
        {
            try { if (townStatement != null) townStatement.close(); }
            catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void setJailedDB()
    {
        PreparedStatement jailedStatement = null;

        String jailedString = "";

        for (UUID UUID : jailedList)
            jailedString += UUID + " ";

        try
        {
            jailedStatement = jailedDBConn.prepareStatement("INSERT INTO `jailedTable` " +
                                                             "(`jailed`) " +
                                                             "VALUES " +
                                                             "(?)");
            jailedStatement.setString(1, jailedString);

            jailedStatement.executeUpdate();
        }
        catch (SQLException e) { TownyBattle.getInstance().printWarn(e, "setJailed error"); }
        finally
        {
            try { if (jailedStatement != null) jailedStatement.close(); }
            catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
