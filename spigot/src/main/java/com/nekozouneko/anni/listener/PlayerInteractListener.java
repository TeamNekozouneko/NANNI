package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.gui.BuffMenu;
import com.nekozouneko.anni.util.BooleanDataType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

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

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            NamespacedKey buffitem = new NamespacedKey(ANNIPlugin.getInstance(), "buffmenu");
            if (e.getItem() != null && e.getItem().getItemMeta() != null) {
                if (
                        e.getItem().getItemMeta().getPersistentDataContainer().has(
                                buffitem, PersistentDataType.BYTE
                        ) && e.getItem().getType() == Material.NETHER_STAR
                ) {
                    Boolean b = e.getItem().getItemMeta().getPersistentDataContainer().get(buffitem, new BooleanDataType());
                    if (b != null && b) {
                        BuffMenu.open(e.getPlayer());
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

}
