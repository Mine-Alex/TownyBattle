package xyz.minealex.townybattle.db;

import java.util.List;
import java.util.UUID;

public class Battle
{
    private final String battleName;
    private final List<UUID> towns;
    private boolean isActive;

    public Battle(String battleName, List<UUID> towns, boolean isActive)
    {
        this.battleName = battleName;
        this.towns = towns;
        this.isActive = isActive;
    }

    public String getBattleName() { return battleName; }
    public List<UUID> getTowns() { return towns; }
    public boolean isActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
}
