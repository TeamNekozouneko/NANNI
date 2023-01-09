package com.nekozouneko.anni.task;

import com.nekozouneko.anni.ANNIPlugin;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class SpectateKiller extends BukkitRunnable {

    private boolean isFirst = true;
    private int timer;
    private final Player player;
    private final Player killer;
    private final ANNIPlugin plugin = ANNIPlugin.getInstance();

    public SpectateKiller(int i, Player p, Player k) {
        this.timer = i;
        this.player = p;
        this.killer = k;
    }

    @Override
    public void run() {
        if (isFirst) {
            isFirst = false;
            if (killer != null) {
                if (killer.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    Location ld = player.getLastDeathLocation();
                    if (ld != null) {
                        player.teleport(ld);
                    }
                    else player.teleport(ANNIPlugin.getGM().getGame().getTeamSpawnPoint(player));
                } else player.teleport(killer);
            }
            else player.teleport(ANNIPlugin.getGM().getGame().getTeamSpawnPoint(player));
        }

        if (timer <= 0) {
            player.addPotionEffect(
                    new PotionEffect(
                            PotionEffectType.BLINDNESS,
                            60, 1,
                            false, false, true
                    )
            );
            player.addPotionEffect(
                    new PotionEffect(
                            PotionEffectType.DAMAGE_RESISTANCE,
                            200, 255,
                            false, false, true
                    )
            );
            player.sendTitle("§aリスポーン中...", "", 0, 60, 10);
            if (ANNIPlugin.getGM().getGame().getPlayerJoinedTeam(player) != null && ANNIPlugin.getGM().getGame().getPlayerJoinedTeam(player).isSpectator()) player.setGameMode(GameMode.SPECTATOR);
            else player.setGameMode(GameMode.SURVIVAL);
            player.teleport(ANNIPlugin.getGM().getGame().getTeamSpawnPoint(player));
            cancel();
        } else {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendTitle("§c死んでしまった！", "リスポーンまであと" + timer + "秒", 0, 25, 0);
            this.timer--;
        }
    }

}
