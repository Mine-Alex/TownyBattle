package xyz.minealex.townybattle.handler;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import xyz.minealex.townybattle.config.LanguageConfig;
import xyz.minealex.townybattle.db.DB;

public class SendCommand implements Listener
{
    @EventHandler
    public void onSendCommand(PlayerCommandPreprocessEvent event)
    {
        Player player = event.getPlayer();

        if (DB.getInstance().getJailedList().contains(player.getUniqueId()))
        {
            event.setCancelled(true);
            player.sendMessage(LanguageConfig.getInstance().getWaitBattleEnd());
        }
    }
}
