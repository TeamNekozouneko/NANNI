package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    @EventHandler
    public void onEvent(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        ANNIPlugin.getGM().leaveFromAllGames(p);
    }

}
