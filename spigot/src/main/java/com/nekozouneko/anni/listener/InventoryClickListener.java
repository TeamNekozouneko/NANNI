package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.gui.GameShopMenu;
import com.nekozouneko.anni.gui.KitEditor;
import com.nekozouneko.anni.gui.KitMenu;
import com.nekozouneko.anni.gui.TeamSelector;

import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;

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
    }

}
