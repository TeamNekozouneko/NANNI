package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class EnchantListener implements Listener {

    @EventHandler
    public void onEnchant(EnchantItemEvent e) {
        Bukkit.getScheduler().runTaskLater(ANNIPlugin.getInstance(), () -> e.getEnchanter().giveExpLevels(-(e.getExpLevelCost()-(e.whichButton()+1))), 3L);
    }

}
