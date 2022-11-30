package com.nekozouneko.anni.gui;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.game.MapManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MapEditor {

    private MapEditor() {}

    public static void open(String m, Player p) {
        final MapManager mm = ANNIPlugin.getMM();

        if (!mm.getMaps().containsKey(m)) return;
        Inventory inv = Bukkit.createInventory(null, 9*3, m + " - マップエディタ");
    }

    public static boolean isHandleable(InventoryClickEvent e) {
        return (
                e.getInventory().getSize() == 9*3
                && e.getView().getTitle().endsWith(" - マップエディタ")
        );
    }

    public static void handle(InventoryClickEvent e) {
        Player p = ((Player) e.getWhoClicked());
        ItemStack cl = e.getCurrentItem();


    }

}
