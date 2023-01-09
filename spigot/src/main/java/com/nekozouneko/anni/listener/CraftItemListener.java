package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import java.util.Arrays;

public class CraftItemListener implements Listener {

    private final Material[] ignoreCraftMaterial = new Material[] {
            Material.ENCHANTING_TABLE, Material.FLINT_AND_STEEL,
            Material.BUCKET, Material.END_CRYSTAL
    };

    @EventHandler
    public void onEvent(CraftItemEvent e) {
        if (Arrays.asList(ignoreCraftMaterial).contains(e.getRecipe().getResult().getType()) && ANNIPlugin.getGM().getGame().isJoined((Player) e.getWhoClicked())) {
            e.setCancelled(true);
        }
    }

}
