package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.gui.*;

import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onEvent(InventoryClickEvent e) {

        if (e.getCurrentItem() == null || e.getCurrentItem().getType().isAir()) return;

        if (TeamSelector.isHandleable(e)) {
            TeamSelector.handle(e);
        }
        else if (KitEditor.isHandleable(e)) {
            KitEditor.handle(e);
        }
        else if (KitMenu.isHandleable(e)) {
            KitMenu.handle(e);
        }
        else if (GameShopMenu.isHandleable(e)) {
            GameShopMenu.handle(e);
        }
        else if (BuffMenu.isHandleable(e)) {
            BuffMenu.handle(e);
        }
        else if (
                !(
                        e.getInventory().getType() == InventoryType.CREATIVE
                        || e.getInventory().getType() == InventoryType.PLAYER
                        || e.getInventory().getType() == InventoryType.CRAFTING
                ) && (
                        e.getCurrentItem().getItemMeta() != null && e.getCurrentItem().getItemMeta().getLore() != null
                        && (e.getCurrentItem().getItemMeta().getLore().contains("§8Kit item")
                        || e.getCurrentItem().getItemMeta().getLore().contains("§8Soulbound"))
                )
        ) e.setCancelled(true);
    }

}
