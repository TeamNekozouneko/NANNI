package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.ANNIUtil;
import com.nekozouneko.anni.game.ANNIGame;
import com.nekozouneko.anni.task.SpectateKiller;
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

import java.util.ArrayList;
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

                g.setKitToPlayer(p);
                /*Bukkit.getScheduler().runTaskLater(plugin, () -> p.addPotionEffects(Arrays.asList(
                        new PotionEffect(
                                PotionEffectType.BLINDNESS, 60, 1,
                                false, false, false
                        ),
                        new PotionEffect(
                                PotionEffectType.DAMAGE_RESISTANCE, 200, 255,
                                false, false, true
                        )
                )), 5);

                p.sendTitle("§aリスポーン中...", "", 0, 60, 10);
                p.setGameMode(GameMode.SURVIVAL);*/
                new SpectateKiller(5, p).runTaskTimer(plugin, 20, 20);
            } else {
                ANNIPlugin.teleportToLobby(p);
                g.removeSavedInventory(p.getUniqueId());
                p.setGameMode(GameMode.ADVENTURE);
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
            ANNIPlugin.getANNIDB().addDeathCount(killed.getUniqueId());
            if (killer != null) {
                ANNIPlugin.getANNIDB().addKillCount(killer.getUniqueId());
                g.broadcast("§7" + ANNIUtil.teamPrefixSuffixAppliedName(killed) + " §f<- §7" + ANNIUtil.teamPrefixSuffixAppliedName(killer));

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
                            || o.getType() == Material.STONE_PICKAXE || o.getType() == Material.WOODEN_SHOVEL
                            || o.getType() == Material.BREAD)
                            && o.getEnchantments().size() == 0)
                    ).forEach(filtered::add);

            e.getDrops().clear();
            e.getDrops().addAll(filtered);
        }

    }

}
