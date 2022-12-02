package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.task.SpectateKiller;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerKillListener implements Listener {

    private final ANNIPlugin plugin = ANNIPlugin.getInstance();

    @EventHandler
    public void onEvent(PlayerDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        Player killed = e.getEntity();

        if (killer != null && killer != killed) {
            World w = e.getEntity().getWorld();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getWorld() == w) {
                    p.sendMessage("§7" + killed.getDisplayName() + " §f<- §7" + killer.getDisplayName());
                }
            }

            e.setDeathMessage(null);
            e.setDroppedExp(0);

            killed.teleport(killer);
            killed.setGameMode(GameMode.SPECTATOR);
            new SpectateKiller(5, killed, killer).runTaskTimer(plugin, 20, 20);
        }

    }

}
