package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import org.bukkit.GameMode;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onEvent(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        e.getPlayer().setGameMode(GameMode.ADVENTURE);
        ANNIPlugin.teleportToLobby(e.getPlayer());
    }

}
