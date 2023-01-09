package com.nekozouneko.anni.listener;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDropItemListener implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        ItemStack drop = e.getItemDrop().getItemStack();

        if (drop.hasItemMeta()) {
            if (drop.getItemMeta().hasLore()) {
                if (drop.getItemMeta().getLore().contains("§8Kit item")) {
                    e.getItemDrop().remove();
                    e.getPlayer().sendMessage("§cキット付属のアイテムのため削除されました。");
                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 2f);
                }
                else if (drop.getItemMeta().getLore().contains("§8Undroppable item")) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage("§cドロップ禁止のアイテムのためキャンセルされました。");
                }
            }
        }
    }

}
