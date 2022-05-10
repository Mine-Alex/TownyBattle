package xyz.minealex.townybattle.command;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.minealex.townybattle.config.LanguageConfig;
import xyz.minealex.townybattle.db.Battle;
import xyz.minealex.townybattle.db.DB;
import xyz.minealex.townybattle.util.CommandUtil;

import java.util.*;

public class TownyBattleCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender,
                             Command cmd,
                             String s,
                             String[] args)
    {
        try
        {
            if (!sender.hasPermission("additionalTownyInformation"))
            {
                sender.sendMessage(LanguageConfig.getInstance().getError());
                return true;
            }

            if (args.length == 0) sender.sendMessage(LanguageConfig.getInstance().getError());

            if (args.length == 1)
            {
                if ("list".equalsIgnoreCase(args[0])) parseList(sender);
                else sender.sendMessage(LanguageConfig.getInstance().getError());
            }

            if (args.length == 2)
            {
                String battleName = args[1];

                switch (args[0].toLowerCase())
                {
                    case "newbattle" -> parseNewBattle(battleName);
                    case "startbattle" -> parseStartBattle(battleName, sender);
                    case "endbattle" -> parseEndBattle(battleName, sender);
                    case "deletebattle" -> parseDeleteBattle(battleName);
                    default -> sender.sendMessage(LanguageConfig.getInstance().getError());
                }
            }

            if (args.length >= 3)
            {
                String battleName = args[1];
                String value = args[2];

                switch (args[0].toLowerCase())
                {
                    case "addnation" -> parseAddNation(battleName, value);
                    case "removenation" -> parseRemoveNation(battleName, value);
                    case "addtown" -> parseAddTown(battleName, value);
                    case "removetown" -> parseRemoveTown(battleName, value);
                    default -> sender.sendMessage(LanguageConfig.getInstance().getError());
                }
            }
        }
        catch (Exception e) { sender.sendMessage(LanguageConfig.getInstance().getError()); e.printStackTrace(); }

        return true;
    }

    private void parseNewBattle(String battleName)
    {
        DB.getInstance().getBattleList().add(new Battle(battleName, new ArrayList<>(), false));
    }

    private void parseDeleteBattle(String battleName)
    {
        DB.getInstance().getBattleList().removeIf(battle -> battle.getBattleName().equals(battleName));
    }

    private void parseAddNation(String battleName, String value)
    {
        List<UUID> townUUIDList = new ArrayList<>();
        Nation nation = TownyAPI.getInstance().getNation(value);
        nation.getTowns().forEach(town -> townUUIDList.add(town.getUUID()));

        for (Battle battle : DB.getInstance().getBattleList())
            if (battle.getBattleName().equalsIgnoreCase(battleName))
            {
                List<UUID> townList = battle.getTowns();
                Set<UUID> townSet = new HashSet<>(townList);
                townList.clear();
                townList.addAll(townSet);
                break;
            }
    }

    private void parseRemoveNation(String battleName, String value)
    {
        List<UUID> townUUIDList = new ArrayList<>();
        Nation nation = TownyAPI.getInstance().getNation(value);
        nation.getTowns().forEach(town -> townUUIDList.add(town.getUUID()));

        for (Battle battle : DB.getInstance().getBattleList())
            if (battle.getBattleName().equalsIgnoreCase(battleName))
            {
                battle.getTowns().removeAll(townUUIDList);
                break;
            }
    }

    private void parseAddTown(String battleName, String value)
    {
        for (Battle battle : DB.getInstance().getBattleList())
            if (battle.getBattleName().equalsIgnoreCase(battleName))
            {
                List<UUID> townUUIDList = battle.getTowns();
                UUID townUUID = TownyAPI.getInstance().getTown(value).getUUID();

                if (!townUUIDList.contains(townUUID))
                    townUUIDList.add(townUUID);

                break;
            }
    }

    private void parseRemoveTown(String battleName, String value)
    {
        for (Battle battle : DB.getInstance().getBattleList())
            if (battle.getBattleName().equalsIgnoreCase(battleName))
            {
                battle.getTowns().remove(TownyAPI.getInstance().getTown(value).getUUID());
                break;
            }
    }

    private void parseList(CommandSender sender)
    {
        StringBuilder battleListStr = new StringBuilder();

        for (Battle battle : DB.getInstance().getBattleList())
        {
            battleListStr.append(LanguageConfig.getInstance().getBattleName()).append(": ").append(battle.getBattleName()).append("\n").append(LanguageConfig.getInstance().getTowns()).append(": \n");

            for (UUID UUID: battle.getTowns())
                battleListStr.append("  ").append(TownyUniverse.getInstance().getTown(UUID).getName()).append("\n");

            battleListStr.append(LanguageConfig.getInstance().getActive()).append(": ").append(battle.isActive()).append("\n");
        }

        sender.sendMessage(battleListStr.toString());
    }

    private void parseStartBattle(String battleName, CommandSender sender)
    {
        Battle battle = DB.getInstance().getBattleByName(battleName);

        if (battle == null || battle.isActive())
        {
            sender.sendMessage(LanguageConfig.getInstance().getError());
            return;
        }

        List<UUID> townUUIDList = battle.getTowns();

        if (townUUIDList.size() < 2)
        {
            sender.sendMessage(LanguageConfig.getInstance().getError());
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers())
        {
            Town playerTown;

            try { playerTown = TownyAPI.getInstance().getResident(player).getTown(); }
            catch (NotRegisteredException e)
            {
                CommandUtil.doActionForNonMembers(player, true);
                continue;
            }

            if (townUUIDList.contains(playerTown.getUUID()))
                CommandUtil.sendBattleActionMessage(player, true, true);
            else
                CommandUtil.doActionForNonMembers(player, true);
        }

        for (UUID townUUID : townUUIDList)
            CommandUtil.setPermissions(Objects.requireNonNull(TownyAPI.getInstance().getTown(townUUID)), true);

        battle.setIsActive(true);
    }

    private void parseEndBattle(String battleName, CommandSender sender)
    {
        Battle battle = DB.getInstance().getBattleByName(battleName);

        if (battle == null || !battle.isActive())
        {
            sender.sendMessage(LanguageConfig.getInstance().getError());
            return;
        }

        List<UUID> townUUIDList = battle.getTowns();

        for (OfflinePlayer player : Bukkit.getOfflinePlayers())
        {
            Town playerTown;

            try { playerTown = TownyAPI.getInstance().getResident(player.getUniqueId()).getTown(); }
            catch (NotRegisteredException ignore) { continue; }

            if (townUUIDList.contains(playerTown.getUUID()))
                DB.getInstance().getJailedList().remove(player.getUniqueId());
        }

        for (Player player : Bukkit.getOnlinePlayers())
        {
            Town playerTown;

            try { playerTown = TownyAPI.getInstance().getResident(player).getTown(); }
            catch (NotRegisteredException e)
            {
                CommandUtil.doActionForNonMembers(player, false);
                continue;
            }

            if (townUUIDList.contains(playerTown.getUUID()))
                CommandUtil.sendBattleActionMessage(player, false, true);
            else
                CommandUtil.doActionForNonMembers(player, false);
        }

        for (UUID townUUID : townUUIDList)
            CommandUtil.setPermissions(Objects.requireNonNull(TownyAPI.getInstance().getTown(townUUID)), false);

        battle.setIsActive(false);
    }
}
