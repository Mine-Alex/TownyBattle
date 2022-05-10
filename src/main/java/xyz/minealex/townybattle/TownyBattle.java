package xyz.minealex.townybattle;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.minealex.townybattle.command.TownyBattleCommand;
import xyz.minealex.townybattle.command.TownyBattleTab;
import xyz.minealex.townybattle.config.LanguageConfig;
import xyz.minealex.townybattle.config.PluginConfig;
import xyz.minealex.townybattle.db.DB;
import xyz.minealex.townybattle.handler.*;

import java.util.logging.Logger;

public final class TownyBattle extends JavaPlugin
{
    private final Logger logger = getLogger();
    private final PluginManager pluginManager = Bukkit.getPluginManager();
    private static TownyBattle instance;

    public static TownyBattle getInstance() { return instance; }

    @Override
    public void onEnable()
    {
        instance = this;

        PluginConfig.getInstance();
        LanguageConfig.getInstance();
        DB.getInstance();

        Bukkit.getPluginManager().registerEvents(new TownDelete(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
        Bukkit.getPluginManager().registerEvents(new SendCommand(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerRespawn(), this);

        getCommand("townyBattle").setExecutor(new TownyBattleCommand());
        getCommand("townyBattle").setTabCompleter(new TownyBattleTab());
    }

    @Override
    public void onDisable()
    {
        DB.getInstance().closeConnection();
    }

    public void disable(Exception reason, String message)
    {
        logger.info(message);
        reason.printStackTrace();
        pluginManager.disablePlugin(this);
    }

    public void printWarn(Exception reason, String message)
    {
        logger.info(message);
        reason.printStackTrace();
    }
}
