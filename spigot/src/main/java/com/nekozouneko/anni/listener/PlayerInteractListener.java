package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (ANNIPlugin.getGM().getGame().isJoined(e.getPlayer()) && ANNIPlugin.getGM().getGame().getStatus().getPhaseId() >= 1) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null) {
                if (e.getClickedBlock().getType().name().matches("^.+_BED$")) {
                    e.getClickedBlock().breakNaturally();
                    e.setCancelled(true);
                }
            }
        }
    }

}
