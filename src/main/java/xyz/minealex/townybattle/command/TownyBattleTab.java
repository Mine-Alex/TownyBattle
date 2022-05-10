package xyz.minealex.townybattle.command;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import xyz.minealex.townybattle.db.Battle;
import xyz.minealex.townybattle.db.DB;

import java.util.*;

public class TownyBattleTab implements TabCompleter
{
    private final List<String> commandList = Arrays.asList("newbattle", "addnation", "removenation", "addtown", "removetown", "list", "startbattle", "endbattle", "deletebattle");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args)
    {
        List<String> tabList = new ArrayList<>();

        if (!sender.hasPermission("battleAdmin")) return tabList;

        switch (args.length)
        {
            case 1: tabList = commandList; break;
            case 2:
                switch (args[0].toLowerCase())
                {
                    case "startbattle":
                        for (Battle battle : DB.getInstance().getBattleList())
                            if (!battle.isActive()) tabList.add(battle.getBattleName());
                        break;
                    case "endbattle":
                        for (Battle battle : DB.getInstance().getBattleList())
                            if (battle.isActive()) tabList.add(battle.getBattleName());
                        break;
                    case "removenation":
                    case "removetown":
                    case "addtown":
                    case "addnation":
                    case "deletebattle":
                        for (Battle battle : DB.getInstance().getBattleList()) tabList.add(battle.getBattleName()); break;
                }
                break;

            case 3:
                switch (args[0].toLowerCase())
                {
                    case "removenation":
                    {
                        Battle battle = DB.getInstance().getBattleByName(args[1]);
                        if (battle == null) break;
                        for (UUID townUUID : battle.getTowns())
                        {
                            Town town = TownyAPI.getInstance().getTown(townUUID);
                            if (town == null) continue;
                            Nation nation = null;
                            try { nation = town.getNation(); }
                            catch (NotRegisteredException e) { e.printStackTrace(); }
                            if (nation == null) continue;
                            tabList.add(nation.getName());
                        }
                        break;
                    }

                    case "removetown":
                    {
                        Battle battle = DB.getInstance().getBattleByName(args[1]);
                        if (battle == null) break;
                        for (UUID townUUID : battle.getTowns())
                        {
                            Town town = TownyAPI.getInstance().getTown(townUUID);
                            if (town == null) continue;
                            tabList.add(town.getName());
                        }
                        break;
                    }

                    case "addnation":
                        for (Nation nation : TownyUniverse.getInstance().getNations()) tabList.add(nation.getName());
                        break;

                    case "addtown":
                        for (Town town: TownyUniverse.getInstance().getTowns()) tabList.add(town.getName());
                        break;
                }
        }

        return tabList;
    }
}
