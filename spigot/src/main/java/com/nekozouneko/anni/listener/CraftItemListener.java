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
            Material.BLACK_BED, Material.BLUE_BED, Material.BROWN_BED,
            Material.CYAN_BED, Material.GRAY_BED, Material.GREEN_BED,
            Material.LIGHT_BLUE_BED, Material.LIGHT_GRAY_BED,
            Material.LIME_BED, Material.MAGENTA_BED, Material.ORANGE_BED,
            Material.PINK_BED, Material.PURPLE_BED, Material.RED_BED,
            Material.WHITE_BED, Material.YELLOW_BED,
            Material.BEACON, Material.ENDER_CHEST
    };

    @EventHandler
    public void onEvent(CraftItemEvent e) {
        if (Arrays.asList(ignoreCraftMaterial).contains(e.getRecipe().getResult().getType()) && ANNIPlugin.getGM().getGame().isJoined((Player) e.getWhoClicked())) {
            e.setCancelled(true);
        }
    }

}
