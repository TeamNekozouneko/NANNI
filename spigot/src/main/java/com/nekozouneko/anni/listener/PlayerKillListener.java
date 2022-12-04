package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.game.ANNIGame;
import com.nekozouneko.anni.task.SpectateKiller;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;

public class PlayerKillListener implements Listener {

    private final ANNIPlugin plugin = ANNIPlugin.getInstance();

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        final Player p = e.getPlayer();
        final ANNIGame g = ANNIPlugin.getGM().getGame();

        if (g.getManager().isJoined(p)) {
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

            e.setDeathMessage(null);
            e.setDroppedExp(0);
        }

    }

}
