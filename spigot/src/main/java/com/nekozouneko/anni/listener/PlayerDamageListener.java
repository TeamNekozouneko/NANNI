package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.util.BooleanDataType;
import com.nekozouneko.nutilsxlib.chat.NChatColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.potion.PotionEffectType;

public class PlayerDamageListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = ((Player) e.getEntity());

            final NamespacedKey nofall = new NamespacedKey(ANNIPlugin.getInstance(), "specialbuffarmor");

            if (p.getInventory().getBoots() != null) {
                ItemStack boot = p.getInventory().getBoots();

                if (boot.getItemMeta() != null) {
                    PersistentDataContainer pdc = boot.getItemMeta().getPersistentDataContainer();
                    if (pdc.has(nofall, new BooleanDataType())) {
                        if (pdc.get(nofall, new BooleanDataType()) == true) {
                            if (e.getCause() == EntityDamageEvent.DamageCause.FALL && !e.isCancelled()) {
                                Damageable d = (Damageable) p.getInventory().getBoots().getItemMeta();
                                if (e.getDamage() < 20) {
                                    d.setDamage(d.getDamage() + 9);
                                } else d.setDamage(d.getDamage() + ((int)e.getDamage()/2));

                                if (d.getDamage() > boot.getType().getMaxDurability()) {
                                    d.setDamage(boot.getType().getMaxDurability());
                                    /*p.getInventory().setBoots(null);
                                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                                    p.spawnParticle(Particle.ITEM_CRACK, p.getLocation(), 10);
                                    Bukkit.getServer().getPluginManager().callEvent(new PlayerItemBreakEvent(p, boot));*/
                                } else e.setCancelled(true);
                                p.getInventory().getBoots().setItemMeta(d);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEvent(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player p = (Player) e.getEntity();

            if (!e.isCancelled()) {
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

}
