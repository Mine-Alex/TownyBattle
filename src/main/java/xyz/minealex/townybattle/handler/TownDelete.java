package xyz.minealex.townybattle.handler;

import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.minealex.townybattle.db.Battle;
import xyz.minealex.townybattle.db.DB;

import java.util.List;
import java.util.UUID;

public class TownDelete implements Listener
{
    @EventHandler
    public void onTownDelete(DeleteTownEvent event)
    {
        UUID townUUID = event.getTownUUID();
        List<Battle> battleList = DB.getInstance().getBattleList();
        battleList.forEach(battle -> battle.getTowns().remove(townUUID));
    }
}
