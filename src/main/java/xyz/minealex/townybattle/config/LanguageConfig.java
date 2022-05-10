package xyz.minealex.townybattle.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.minealex.townybattle.TownyBattle;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LanguageConfig
{
    private FileConfiguration configuration;
    private String titleBattleEnd, subtitleBattleEnd, titleBattleStart, subtitleBattleStart, messageBattleStart,
            messageBattleEnd, error, waitBattleEnd, battleName, towns, active;

    private static LanguageConfig instance;

    public static LanguageConfig getInstance()
    {
        if (instance == null) instance = new LanguageConfig();

        return instance;
    }

    public LanguageConfig()
    {
        try
        {
            configuration =
                    YamlConfiguration.loadConfiguration(
                            new BufferedReader(
                                    new InputStreamReader(
                                            getClass().getResourceAsStream("/lang/" + PluginConfig.getInstance().getLanguage() + ".yml")
            )));

            updateConfig();
        }
        catch (Exception e) { TownyBattle.getInstance().disable(e, "Load language error"); }
    }

    public void updateConfig()
    {
        titleBattleEnd = configuration.getString("titleBattleEnd");
        subtitleBattleEnd = configuration.getString("subtitleBattleEnd");
        titleBattleStart = configuration.getString("titleBattleStart");
        subtitleBattleStart = configuration.getString("subtitleBattleStart");
        messageBattleStart = configuration.getString("messageBattleStart");
        messageBattleEnd = configuration.getString("messageBattleEnd");
        error = configuration.getString("error");
        waitBattleEnd = configuration.getString("waitBattleEnd");
        battleName = configuration.getString("battleName");
        active = configuration.getString("active");
        towns = configuration.getString("towns");
    }

    public String getMessageBattleStart() { return messageBattleStart; }
    public String getMessageBattleEnd() { return messageBattleEnd; }
    public String getError() { return error; }
    public String getWaitBattleEnd() { return waitBattleEnd; }
    public String getTitleBattleEnd() { return titleBattleEnd; }
    public String getSubtitleBattleEnd() { return subtitleBattleEnd; }
    public String getTitleBattleStart() { return titleBattleStart; }
    public String getSubtitleBattleStart() { return subtitleBattleStart; }
    public String getBattleName() { return battleName; }
    public String getTowns() { return towns; }
    public String getActive() { return active; }
}
