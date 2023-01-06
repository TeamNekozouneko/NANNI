package com.nekozouneko.anni.listener.third_party;

import com.nekozouneko.anni.ANNIPlugin;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VoteListener implements Listener {

    @EventHandler
    public void onVote(VotifierEvent e) {
        OfflinePlayer off = Bukkit.getOfflinePlayer(e.getVote().getUsername());

        Bukkit.broadcastMessage("§7[§a投票通知@"+e.getVote().getServiceName()+"§7] " + e.getVote().getUsername() + "が投票しました！");
        if (!ANNIPlugin.getVaultEconomy().hasAccount(off)) {
            if (ANNIPlugin.getVaultEconomy().createPlayerAccount(off)) {
                ANNIPlugin.getVaultEconomy().depositPlayer(off, 1000.);
            }
        } else ANNIPlugin.getVaultEconomy().depositPlayer(off, 1000.);
    }

}
