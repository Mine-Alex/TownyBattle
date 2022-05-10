package xyz.minealex.townybattle.handler;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import xyz.minealex.townybattle.config.LanguageConfig;
import xyz.minealex.townybattle.config.PluginConfig;

public class PlayerDeath implements Listener
{
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();

        if (player.hasPermission("battleAdmin")) return;

        if (PluginConfig.getInstance().getJailCoordinates().equals(""))
            player.kickPlayer(LanguageConfig.getInstance().getWaitBattleEnd());
    }
}
