package xyz.minealex.townybattle.config;

import org.bukkit.configuration.file.FileConfiguration;
import xyz.minealex.townybattle.TownyBattle;

public class PluginConfig
{
    private final FileConfiguration configuration;
    private boolean enablePVP, enableFire, enableMobs, enableExplosion, enableOutsiderItemUse, enableOutsiderBuild,
                    enableOutsiderSwitch, enableOutsiderDestroy, kickNonMembers, sendMessageToEveryone, seeTitleToEveryone,
                    playSoundToEveryone;
    private String language, battleEndSound, battleStartSound, jailCoordinates;
    private static PluginConfig instance;

    public static PluginConfig getInstance()
    {
        if (instance == null) instance = new PluginConfig();

        return instance;
    }

    public PluginConfig()
    {
        configuration = TownyBattle.getInstance().getConfig();
        configuration.options().copyDefaults(true);

        TownyBattle.getInstance().saveDefaultConfig();

        configuration.addDefault("enablePVP", true);
        configuration.addDefault("enableFire", true);
        configuration.addDefault("enableMobs", false);
        configuration.addDefault("enableExplosion", true);
        configuration.addDefault("enableOutsiderItemUse", true);
        configuration.addDefault("enableOutsiderBuild", true);
        configuration.addDefault("enableOutsiderSwitch", true);
        configuration.addDefault("enableOutsiderDestroy", false);
        configuration.addDefault("kickNonMembers", false);
        configuration.addDefault("sendMessageToEveryone", true);
        configuration.addDefault("seeTitleToEveryone", false);
        configuration.addDefault("playSoundToEveryone", false);
        configuration.addDefault("language", "en");
        configuration.addDefault("battleEndSound", "AMBIENT_CAVE");
        configuration.addDefault("battleStartSound", "ENTITY_PLAYER_LEVELUP");
        configuration.addDefault("jailCoordinates", "");

        updateConfig();

        TownyBattle.getInstance().saveConfig();
    }

    public void updateConfig()
    {
        enablePVP = configuration.getBoolean("enablePVP");
        enableFire = configuration.getBoolean("enableFire");
        enableMobs = configuration.getBoolean("enableMobs");
        enableExplosion = configuration.getBoolean("enableExplosion");
        enableOutsiderItemUse = configuration.getBoolean("enableOutsiderItemUse");
        enableOutsiderBuild = configuration.getBoolean("enableOutsiderBuild");
        enableOutsiderSwitch = configuration.getBoolean("enableOutsiderSwitch");
        enableOutsiderDestroy = configuration.getBoolean("enableOutsiderDestroy");
        kickNonMembers = configuration.getBoolean("kickNonMembers");
        sendMessageToEveryone = configuration.getBoolean("sendMessageToEveryone");
        seeTitleToEveryone = configuration.getBoolean("seeTitleToEveryone");
        playSoundToEveryone = configuration.getBoolean("playSoundToEveryone");
        language = configuration.getString("language");
        battleEndSound = configuration.getString("battleEndSound");
        battleStartSound = configuration.getString("battleStartSound");
        jailCoordinates = configuration.getString("jailCoordinates");
    }

    public boolean isEnablePVP() { return enablePVP; }
    public boolean isEnableFire() { return enableFire; }
    public boolean isEnableMobs() { return enableMobs; }
    public boolean isEnableExplosion() { return enableExplosion; }
    public boolean isEnableOutsiderItemUse() { return enableOutsiderItemUse; }
    public boolean isEnableOutsiderBuild() { return enableOutsiderBuild; }
    public boolean isEnableOutsiderSwitch() { return enableOutsiderSwitch; }
    public boolean isEnableOutsiderDestroy() { return enableOutsiderDestroy; }
    public boolean isKickNonMembers() { return kickNonMembers; }
    public boolean isSendMessageToEveryone() { return sendMessageToEveryone; }
    public boolean isSeeTitleToEveryone() { return seeTitleToEveryone; }
    public boolean isPlaySoundToEveryone() { return playSoundToEveryone; }
    public String getLanguage() { return language; }
    public String getBattleEndSound() { return battleEndSound; }
    public String getBattleStartSound() { return battleStartSound; }
    public String getJailCoordinates() { return jailCoordinates; }
}
