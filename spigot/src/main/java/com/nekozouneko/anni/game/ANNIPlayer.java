package com.nekozouneko.anni.game;

import org.bukkit.entity.Player;

public class ANNIPlayer {

    private final Player bukkitPlayer;

    protected ANNIPlayer(Player p) {
        this.bukkitPlayer = p;
    }

    public Player getBukkitPlayer() {
        return bukkitPlayer;
    }
}
