package com.nekozouneko.anni.event;

import com.nekozouneko.anni.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NexusAttackEvent extends Event {

    private final Player player;
    private final Team team;

    public NexusAttackEvent(Player p, Team t) {
        this.player = p;
        this.team = t;
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }

    public Player getAttacker() {
        return player;
    }

    public Team getAttackedNexusTeam() {
        return team;
    }
}
