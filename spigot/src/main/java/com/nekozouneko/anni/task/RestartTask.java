package com.nekozouneko.anni.task;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.ANNIUtil;
import com.nekozouneko.anni.game.ANNIGame;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RestartTask extends BukkitRunnable {
    private int time;
    private final ANNIGame g;

    public RestartTask(int time, ANNIGame g) {
        this.g = g;
        this.time = time;
    }

    @Override
    public void run() {
        if (time <= 0) {
            for (Player p : g.getPlayers()) {
                p.setGameMode(GameMode.ADVENTURE);
                ANNIUtil.healPlayer(p);
                p.getInventory().clear();
                p.getEnderChest().clear();
                ANNIPlugin.teleportToLobby(p);
            }
            g.restart();
            cancel();
        } else {
            if (g.getNotLostTeams().size() > 1) {
                super.cancel();
            }

            if (time > 5) {
                if (time % 5 == 0) {
                    g.broadcast("再起動まであと" + time + "秒");
                }
            } else {
                g.broadcast("再起動まであと" + time + "秒");
            }

            time--;
        }
    }
}
