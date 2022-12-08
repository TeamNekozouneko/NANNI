package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.ANNIUtil;
import com.nekozouneko.nutilsxlib.chat.NChatColor;
import fr.minuskube.netherboard.Netherboard;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    private final Netherboard nb = ANNIPlugin.getNb();

    @EventHandler
    public void onEvent(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        Player p = e.getPlayer();

        if (ANNIPlugin.getGM().getGame().getPlayers().contains(p)) {
            ANNIPlugin.getGM().getGame().broadcast(
                    ANNIUtil.teamPrefixSuffixAppliedName(p) + NChatColor.YELLOW + " がゲームを退出しました。"
            );
        }

        ANNIPlugin.getGM().leaveFromGame(p);
        if (nb != null) if (nb.getBoard(p) != null) nb.getBoard(p).delete();
    }

}
