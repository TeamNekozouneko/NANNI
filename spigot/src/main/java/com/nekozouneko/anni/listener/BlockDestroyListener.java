package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class BlockDestroyListener implements Listener {

    private final Random r = new Random();
    private final ANNIPlugin plugin = ANNIPlugin.getInstance();

    @EventHandler
    public void onEvent(BlockBreakEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.SURVIVAL) return;

        World w = e.getBlock().getWorld();
        Location loc = e.getBlock().getLocation();

        switch (e.getBlock().getType()) {
            case END_STONE:
                w.spawnParticle(
                        Particle.SMOKE_NORMAL, loc.getX()+0.5, loc.getY() +0.5, loc.getZ()+0.5,
                        100, 0.1, 0.1, 0.1, 0.1
                );
                w.spawnParticle(
                        Particle.LAVA, loc.getX()+0.5, loc.getY() +0.5, loc.getZ()+0.5,
                        50, 0.25, 0.25, 0.25, 0.1
                );
                w.playSound(e.getBlock().getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, r.nextFloat());

                e.setDropItems(false);
                Bukkit.getScheduler().runTaskLater(
                        plugin, () -> loc.getBlock().setType(Material.END_STONE), 3
                );

                break;
            case MELON:
                e.setDropItems(false);
                e.getPlayer().getInventory().addItem(new ItemStack(Material.MELON_SLICE, r.nextInt(8)+2));

                Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(Material.MELON), 100);
                break;
            case END_STONE_BRICKS:
                e.setDropItems(false);
                w.spawnParticle(
                        Particle.SMOKE_NORMAL, loc.getX()+0.5, loc.getY() +0.5, loc.getZ()+0.5,
                        100, 0.1, 0.1, 0.1, 0.1
                );
                w.spawnParticle(
                        Particle.LAVA, loc.getX()+0.5, loc.getY() +0.5, loc.getZ()+0.5,
                        50, 0.25, 0.25, 0.25, 0.1
                );
                w.spawnParticle(
                        Particle.EXPLOSION_HUGE, loc.getX()+0.5, loc.getY() +0.5, loc.getZ()+0.5,
                        3, 1f, 1f, 1f, 0.1
                );
                w.playSound(e.getBlock().getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, r.nextFloat());
                w.playSound(e.getBlock().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 100f, 0f);
                Bukkit.getScheduler().runTaskLater(plugin, () -> loc.getBlock().setType(Material.BEDROCK), 3);
                break;

            default: break;
        }
    }

}
