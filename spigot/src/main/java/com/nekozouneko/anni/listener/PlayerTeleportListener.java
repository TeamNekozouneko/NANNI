package com.nekozouneko.anni.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onEvent(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        if (!p.isOp() || !p.hasPermission("minecraft.command.teleport")) {
            // disable spectator teleport
            if (e.getCause().equals(PlayerTeleportEvent.TeleportCause.SPECTATE)) {
                e.setCancelled(true);
            }
        }
    }

}
