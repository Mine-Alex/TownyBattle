package xyz.minealex.townybattle.util;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyPermission;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import xyz.minealex.townybattle.config.LanguageConfig;
import xyz.minealex.townybattle.config.PluginConfig;

public class CommandUtil
{
    public static void setPermissions(Town town, boolean isStart)
    {
        TownyPermission townyPermission = town.getPermissions();

        if (isStart)
        {
            townyPermission.set("pvp", PluginConfig.getInstance().isEnablePVP());
            townyPermission.set("fire", PluginConfig.getInstance().isEnableFire());
            townyPermission.set("mobs", PluginConfig.getInstance().isEnableMobs());
            townyPermission.set("explosion", PluginConfig.getInstance().isEnableExplosion());
            townyPermission.set("outsiderItemUse", PluginConfig.getInstance().isEnableOutsiderItemUse());
            townyPermission.set("outsiderBuild", PluginConfig.getInstance().isEnableOutsiderBuild());
            townyPermission.set("outsiderSwitch", PluginConfig.getInstance().isEnableOutsiderSwitch());
            townyPermission.set("outsiderDestroy", PluginConfig.getInstance().isEnableOutsiderDestroy());
        }
        else
        {
            townyPermission.set("pvp", false);
            townyPermission.set("fire", false);
            townyPermission.set("mobs", false);
            townyPermission.set("explosion", false);
            townyPermission.set("outsiderItemUse", false);
            townyPermission.set("outsiderBuild", false);
            townyPermission.set("outsiderSwitch", false);
            townyPermission.set("outsiderDestroy", false);
        }
    }

    public static void sendBattleActionMessage(Player player, boolean isStart, boolean isMember)
    {
        if (PluginConfig.getInstance().isSendMessageToEveryone())
        {
            if (isStart)
                player.sendMessage(LanguageConfig.getInstance().getMessageBattleStart());
            else
                player.sendMessage(LanguageConfig.getInstance().getMessageBattleEnd());
        }

        if (isMember || PluginConfig.getInstance().isSeeTitleToEveryone())
        {
            if (isStart)
                player.sendTitle(
                        LanguageConfig.getInstance().getTitleBattleStart(),
                        LanguageConfig.getInstance().getSubtitleBattleStart(),
                        1, 60, 1
                );
            else
                player.sendTitle(
                        LanguageConfig.getInstance().getTitleBattleEnd(),
                        LanguageConfig.getInstance().getSubtitleBattleEnd(),
                        1, 60, 1
                );
        }

        if (isMember || PluginConfig.getInstance().isPlaySoundToEveryone())
        {
            if (isStart)
                player.playSound(
                        player.getLocation(),
                        Sound.valueOf(PluginConfig.getInstance().getBattleStartSound()),
                        2F, 1F
                );
            else
                player.playSound(
                        player.getLocation(),
                        Sound.valueOf(PluginConfig.getInstance().getBattleEndSound()),
                        2F, 1F
                );
        }
    }

    public static void doActionForNonMembers(Player player, boolean isStart)
    {
        if (PluginConfig.getInstance().isKickNonMembers() && !player.hasPermission("battleAdmin") && isStart)
            player.kickPlayer(LanguageConfig.getInstance().getWaitBattleEnd());
        else
            CommandUtil.sendBattleActionMessage(player, isStart, false);
    }
}
