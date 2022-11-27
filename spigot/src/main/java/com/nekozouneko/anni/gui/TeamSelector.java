package com.nekozouneko.anni.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.Scoreboard;

public final class TeamSelector {

    static {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
    }

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

    }

    public static boolean isHandleable(InventoryClickEvent e) {
        return (e.getInventory().getSize() == 9) && (e.getView().getTitle().equals("チームを選択"));
    }

}
