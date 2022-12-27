package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.PortalCreateEvent;

public class PortalActionListener implements Listener {

    @EventHandler
    public void onTravel(PlayerPortalEvent e) {
        if (
                ANNIPlugin.getGM().getGame().isJoined(e.getPlayer())
                && (e.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL
                || e.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL
                || e.getCause() == PlayerTeleportEvent.TeleportCause.END_GATEWAY)
        ) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCreate(PortalCreateEvent e) {
        if (e.getReason() == PortalCreateEvent.CreateReason.FIRE) {
            if (
                    e.getEntity() instanceof Player
                    && ANNIPlugin.getGM().getGame().isJoined((Player)e.getEntity())
            ) {
                e.getEntity().sendMessage("§cゲーム中にポータルを作成することはできません。");
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDestroy(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.NETHER_PORTAL) {
            if (ANNIPlugin.getGM().getGame().isJoined(e.getPlayer())) {
                e.getPlayer().sendMessage("§cポータルを破壊することはできません。");
                e.setCancelled(true);
            }
        }
    }
}
