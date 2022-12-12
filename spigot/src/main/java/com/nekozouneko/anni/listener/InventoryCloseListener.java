package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.gui.KitEditor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {

    @EventHandler
    public void onEvent(InventoryCloseEvent e) {
        if (KitEditor.isHandleable(e)) {
            KitEditor.closeHandle(e);
        }
    }

}
