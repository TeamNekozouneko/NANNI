package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.gui.TeamSelector;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    @EventHandler(ignoreCancelled=true)
    public void onEvent(InventoryClickEvent e) {

        if (e.getCurrentItem() == null || e.getCurrentItem().getType().isAir()) return;

        if (TeamSelector.isHandleable(e)) {
            TeamSelector.handle(e);
        }
    }

}
