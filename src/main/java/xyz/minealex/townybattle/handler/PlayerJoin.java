package xyz.minealex.townybattle.handler;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.minealex.townybattle.config.LanguageConfig;
import xyz.minealex.townybattle.config.PluginConfig;
import xyz.minealex.townybattle.db.Battle;
import xyz.minealex.townybattle.db.DB;

import java.util.Objects;
import java.util.UUID;

public class PlayerJoin implements Listener
{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        if (player.hasPermission("battleAdmin")) return;

        Resident resident = TownyAPI.getInstance().getResident(player);

        try { resident.getTown(); }
        catch (NotRegisteredException e)
        {
            if (PluginConfig.getInstance().isKickNonMembers())
            {
                for (Battle battle : DB.getInstance().getBattleList())
                    if (battle.isActive())
                    {
                        player.kickPlayer(LanguageConfig.getInstance().getWaitBattleEnd());
                        return;
                    }
            }
        }

        for (Battle battle : DB.getInstance().getBattleList())
            if (battle.isActive())
            {
                if (PluginConfig.getInstance().isKickNonMembers())
                {
                    player.kickPlayer(LanguageConfig.getInstance().getWaitBattleEnd());
                    break;
                }
                else for (UUID townUUID : battle.getTowns())
                        if (Objects.requireNonNull(TownyAPI.getInstance().getTown(townUUID)).getResidents().contains(resident))
                        {
                            player.kickPlayer(LanguageConfig.getInstance().getWaitBattleEnd());
                            return;
                        }
            }
    }
}
