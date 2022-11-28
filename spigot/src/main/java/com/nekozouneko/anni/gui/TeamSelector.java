package com.nekozouneko.anni.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public final class TeamSelector {

    private static final Material[] clickToCloseItems = new Material[] {
            Material.RED_WOOL,
            Material.BLUE_WOOL,
            Material.YELLOW_WOOL,
            Material.GREEN_WOOL,

            Material.WHITE_CONCRETE,
            Material.ENDER_PEARL
    };

    private TeamSelector() {}

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, "チームを選択");

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setOwningPlayer(p);
        head.setItemMeta(headMeta);

        inv.setItem(0, head);
        inv.setItem(2, new ItemStack(Material.RED_WOOL));
        inv.setItem(3, new ItemStack(Material.BLUE_WOOL));
        inv.setItem(7, new ItemStack(Material.WHITE_CONCRETE));
        inv.setItem(8, new ItemStack(Material.ENDER_PEARL));

        p.openInventory(inv);
    }

    public static void handle(InventoryClickEvent e) {
        e.setCancelled(true);

        final ItemStack clicked = e.getCurrentItem();
        final Player p = ((Player) e.getView().getPlayer());

        if (clicked == null) return;

        if (Arrays.asList(clickToCloseItems).contains(clicked.getType())) e.getView().close();
    }

    public static boolean isHandleable(InventoryClickEvent e) {
        return (e.getInventory().getSize() == 9) && (e.getView().getTitle().equals("チームを選択"));
    }

}
