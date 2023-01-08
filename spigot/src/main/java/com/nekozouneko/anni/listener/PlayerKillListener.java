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
                new SpectateKiller(5, p, p.getKiller()).runTaskTimer(plugin, 20, 20);
            } else {
                ANNIPlugin.teleportToLobby(p);
                ANNIUtil.healPlayer(p);
                p.getInventory().clear();
                p.getEnderChest().clear();
                p.setLevel(0);
                p.setTotalExperience(0);
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
                ANNIPlugin.getVaultEconomy().depositPlayer(killer, 10.);

                killer.sendMessage("§a+10 Nekozouneko Anni Point §7(NAP)");
                g.broadcast("§7" + ANNIUtil.teamPrefixSuffixAppliedName(killed) + " §f<- §7" + ANNIUtil.teamPrefixSuffixAppliedName(killer));

                /*killed.teleport(killer);
                killed.setGameMode(GameMode.SPECTATOR);
                new SpectateKiller(5, killed, killer).runTaskTimer(plugin, 20, 20);*/
            }

            e.setKeepInventory(true);
            e.setDeathMessage(null);
            e.setDroppedExp(killed.getTotalExperience());
            e.setNewTotalExp(0);

            final List<ItemStack> dropped = new ArrayList<>(e.getDrops());
            List<ItemStack> filtered = new ArrayList<>();
            dropped.stream()
                    .filter(o -> (
                                    !(o.hasItemMeta()
                                    && o.getItemMeta().hasLore()
                                    && o.getItemMeta().getLore().contains("§8Kit Undroppable item"))
                            )
                    ).forEach(filtered::add);

            e.getDrops().clear();
            e.getDrops().addAll(filtered);
        }

    }

}
