package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.ANNIUtil;
import com.nekozouneko.anni.Team;
import com.nekozouneko.nutilsxlib.chat.NChatColor;
import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    @EventHandler
    public void onEvent(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        Player p = e.getPlayer();

        if (ANNIPlugin.getGM().getGame().getPlayers().contains(p)) {
            ANNIPlugin.getGM().getGame().broadcast(
                    ANNIUtil.teamPrefixSuffixAppliedName(p) + NChatColor.YELLOW + " がゲームを退出しました。"
            );
        }

        Team current = ANNIPlugin.getGM().getGame().getPlayerJoinedTeam(p);
        ANNIPlugin.getGM().leaveFromGame(p);
        if (current != null && ANNIPlugin.getGM().getGame().isLose(current)) {
            ANNIPlugin.getGM().getGame().getSavedInventory(p.getUniqueId()).setAllowJoin(false);
        }
        FastBoard fb = ANNIPlugin.getFBMap().get(p.getUniqueId());
        if (fb != null && !fb.isDeleted()) {
            fb.delete();
        }
        ANNIPlugin.getFBMap().remove(p.getUniqueId());
    }

}
