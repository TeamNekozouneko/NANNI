package com.nekozouneko.anni.task;

import com.nekozouneko.anni.ANNIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class SpectateKiller extends BukkitRunnable {

    private int timer;
    private final Player player;
    private final ANNIPlugin plugin = ANNIPlugin.getInstance();

    public SpectateKiller(int i, Player p) {
        this.timer = i;
        this.player = p;
    }

    @Override
    public void run() {
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
            player.setGameMode(GameMode.SURVIVAL);
            cancel();
        } else {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendTitle("§c死んでしまった！", "リスポーンまであと" + timer + "秒", 0, 25, 0);
            this.timer--;
        }
    }

}
