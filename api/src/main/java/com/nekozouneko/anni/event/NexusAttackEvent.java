package com.nekozouneko.anni.event;

import com.nekozouneko.anni.ANNITeam;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NexusAttackEvent extends Event {

    private final Player player;
    private final ANNITeam team;
    private final int damage;

    public NexusAttackEvent(Player p, ANNITeam t, int d) {
        this.player = p;
        this.team = t;
        this.damage = d;
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }

    public Player getAttacker() {
        return player;
    }

    public ANNITeam getAttackedNexusTeam() {
        return team;
    }

    public int getNexusDamage() {
        return damage;
    }
}
