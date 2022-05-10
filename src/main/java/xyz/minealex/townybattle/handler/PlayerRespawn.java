package xyz.minealex.townybattle.handler;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import xyz.minealex.townybattle.config.PluginConfig;
import xyz.minealex.townybattle.db.Battle;
import xyz.minealex.townybattle.db.DB;

public class PlayerRespawn implements Listener
{
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();

        if (player.hasPermission("battleAdmin")) return;

        Town playerTown;

        try { playerTown = TownyAPI.getInstance().getResident(player.getUniqueId()).getTown(); }
        catch (NotRegisteredException ignore) { return; }

        if (!PluginConfig.getInstance().getJailCoordinates().equals(""))
        {
            for (Battle battle : DB.getInstance().getBattleList())
                if (battle.getTowns().contains(playerTown.getUUID()))
                {
                    DB.getInstance().getJailedList().add(player.getUniqueId());
                    String[] locationStr = PluginConfig.getInstance().getJailCoordinates().split(" ");
                    Location teleportLocation = new Location(
                            player.getWorld(),
                            Double.parseDouble(locationStr[0]),
                            Double.parseDouble(locationStr[1]),
                            Double.parseDouble(locationStr[2])
                    );
                    event.setRespawnLocation(teleportLocation);
                    break;
                }
        }
    }
}
