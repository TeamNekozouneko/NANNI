package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import fr.minuskube.netherboard.Netherboard;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    private final Netherboard nb = ANNIPlugin.getNb();

    @EventHandler
    public void onEvent(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        ANNIPlugin.getGM().leaveFromGame(p);
        if (nb.getBoard(p) != null) nb.getBoard(p).delete();
    }

}
