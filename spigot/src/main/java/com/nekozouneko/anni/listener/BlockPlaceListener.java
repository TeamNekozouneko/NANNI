package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.gui.MapEditor;
import com.nekozouneko.anni.gui.location.AbstractGUILocationSelector;
import com.nekozouneko.anni.gui.location.NexusLocation;
import com.nekozouneko.anni.gui.location.TeamSpawnPointLocation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onEvent(BlockPlaceEvent e) {
        AbstractGUILocationSelector g = MapEditor.glc.get(e.getPlayer().getUniqueId());
        if (g != null) {
            if (g instanceof TeamSpawnPointLocation) {
                g.edit(e.getBlock().getLocation());
                e.setCancelled(true);
            }
        }
    }

}
