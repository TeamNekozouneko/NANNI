package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.Team;
import com.nekozouneko.nutilsxlib.chat.NChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

public class PlayerDamageListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onEvent(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player p = (Player) e.getEntity();

            if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                String d = e.getDamager().getCustomName();
                d = d == null ? e.getDamager().getName() : d;
                p.removePotionEffect(PotionEffectType.INVISIBILITY);
                p.sendMessage(NChatColor.RED + d + "の攻撃により透明化が解除されました");
                p.getLocation().getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 0);
            }
        }
    }

}
