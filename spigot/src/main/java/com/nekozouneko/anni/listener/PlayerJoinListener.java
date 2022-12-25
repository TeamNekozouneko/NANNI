package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import org.bukkit.GameMode;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onEvent(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        e.getPlayer().setGameMode(GameMode.ADVENTURE);
        e.getPlayer().getInventory().clear();
        ANNIPlugin.teleportToLobby(e.getPlayer());

        if (!ANNIPlugin.getANNIDB().hasStatsData(e.getPlayer().getUniqueId())) {
            ANNIPlugin.getANNIDB().initPlayerData(e.getPlayer().getUniqueId());
        }
    }

}
