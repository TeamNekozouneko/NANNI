package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.game.ANNIGame;
import com.nekozouneko.anni.task.SpectateKiller;
import com.nekozouneko.anni.util.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerKillListener implements Listener {

    private final ANNIPlugin plugin = ANNIPlugin.getInstance();

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        final Player p = e.getPlayer();
        final ANNIGame g = ANNIPlugin.getGM().getGame();

        if (g.getManager().isJoined(p)) {
            if (g.getStatus().getPhaseId() >= 1 && !g.isLose(g.getPlayerJoinedTeam(p))) {
                Location sl = g.getTeamSpawnPoint(p);
                if (sl != null) {
                    e.setRespawnLocation(sl);
                }

                g.setDefaultKitToPlayer(p);
                Bukkit.getScheduler().runTaskLater(plugin, () -> p.addPotionEffects(Arrays.asList(
                        new PotionEffect(
                                PotionEffectType.BLINDNESS, 60, 1,
                                false, false, false
                        ),
                        new PotionEffect(
                                PotionEffectType.DAMAGE_RESISTANCE, 60, 255,
                                false, false, true
                        )
                )), 5);

                p.sendTitle("§aリスポーン中...", "", 0, 60, 10);
                p.setGameMode(GameMode.SURVIVAL);
            } else {

            }
        }
    }

    @EventHandler
    public void onEvent(PlayerDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        Player killed = e.getEntity();
        final ANNIGame g = ANNIPlugin.getGM().getGame();
        if (g == null) return;

        if (ANNIPlugin.getGM().isJoined(killed)) {
            if (killer != null) {
                g.broadcast("§7" + killed.getDisplayName() + " §f<- §7" + killer.getDisplayName());

                /*killed.teleport(killer);
                killed.setGameMode(GameMode.SPECTATOR);
                new SpectateKiller(5, killed, killer).runTaskTimer(plugin, 20, 20);*/
            }

            e.setKeepInventory(true);
            e.setDeathMessage(null);
            e.setDroppedExp(0);
            e.setNewTotalExp(0);

            final List<ItemStack> dropped = new ArrayList<>(e.getDrops());
            List<ItemStack> filtered = new ArrayList<>();
            dropped.stream()
                    .filter(o ->
                            !((o.getType() == Material.LEATHER_HELMET || o.getType() == Material.LEATHER_CHESTPLATE
                            || o.getType() == Material.LEATHER_LEGGINGS || o.getType() == Material.LEATHER_BOOTS
                            || o.getType() == Material.WOODEN_AXE || o.getType() == Material.WOODEN_SWORD
                            || o.getType() == Material.STONE_PICKAXE || o.getType() == Material.WOODEN_SHOVEL)
                            && o.getEnchantments().size() == 0)
                    ).forEach(filtered::add);

            e.getDrops().clear();
            e.getDrops().addAll(filtered);
        }

    }

}
