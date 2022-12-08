package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.nutilsxlib.chat.NChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

public class PlayerDamageListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onEvent(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();

            if (p.getLocation().getWorld().equals(ANNIPlugin.getLobby().getLocation().getBukkitWorld())) {
                e.setCancelled(true);
                return;
            }

            if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                String d = e.getDamager().getCustomName();
                d = d == null ? e.getDamager().getName() : d;
                p.removePotionEffect(PotionEffectType.INVISIBILITY);
                p.sendMessage(NChatColor.RED + d + "の攻撃により透明化が解除されました");

            }
        }
    }

}
