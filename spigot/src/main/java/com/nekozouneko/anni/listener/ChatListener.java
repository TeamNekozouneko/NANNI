package com.nekozouneko.anni.listener;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.ANNIUtil;
import com.nekozouneko.anni.Team;
import com.nekozouneko.anni.game.ANNIGame;
import com.nekozouneko.anni.game.ANNIStatus;
import com.nekozouneko.nutilsxlib.chat.NChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener implements Listener {

    private static final Character globalPrefix = '!';
    private static final Character tellPrefix = '@';

    @EventHandler
    public void onEvent(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String m = e.getMessage();
        ANNIGame g = ANNIPlugin.getGM().getGame();

        try {
            if (
                    g.isJoined(p)
                    && (g.getStatus().getPhaseId() >= 1
                    || g.getStatus() == ANNIStatus.WAITING_RESTART)
                    && g.getPlayerJoinedTeam(p) != null
            ) {
                if (m.startsWith(globalPrefix.toString()) || g.getPlayerJoinedTeam(p).isSpectator()) {
                    Bukkit.broadcastMessage("§8(Global) §r" + ANNIUtil.teamPrefixSuffixAppliedName(p) + "§f: " + (g.getPlayerJoinedTeam(p).isSpectator() ? m : m.substring(1)));
                    e.setCancelled(true);
                } else if (m.startsWith(tellPrefix.toString())) {
                    List<String> arr = new ArrayList<>(Arrays.asList(m.split(" ")));
                    Matcher match = Pattern.compile("@([0-9A-Za-z_]{2,16})").matcher(arr.get(0));

                    if (match.find()) {
                        String un = match.group(1);
                        Player tar = Bukkit.getPlayer(un);

                        if (tar == null) {
                            List<Player> res = new ArrayList<>();

                            for (Player p1 : Bukkit.getOnlinePlayers()) {
                                String d = p1.getName();

                                if (un.startsWith(d) && g.isJoined(p1) && g.getPlayerJoinedTeam(p1) == g.getPlayerJoinedTeam(p)) {
                                    res.add(p1);
                                }
                            }

                            if (res.size() == 1) {
                                tar = res.get(0);
                            } else {
                                e.getPlayer().sendMessage(NChatColor.RED + "一致するプレイヤーはいないか、2人以上います。");
                                e.setCancelled(true);
                                return;
                            }
                        } else {
                            if (g.isJoined(tar) && g.getPlayerJoinedTeam(tar) != g.getPlayerJoinedTeam(p)) {
                                p.sendMessage(NChatColor.RED + "一致したプレイヤーが別チームのため送信できません。");
                                e.setCancelled(true);
                                return;
                            }
                        }

                        arr.remove(0);

                        String tm = "§7[ "+ANNIUtil.teamPrefixSuffixAppliedName(p)+"§7 ->§r "+ ANNIUtil.teamPrefixSuffixAppliedName(tar) +"§7 ] §r" + String.join(" ", arr);

                        tar.sendMessage(tm);
                        p.sendMessage(tm);

                        e.setCancelled(true);
                    }
                }
                else {
                    Team t = g.getPlayerJoinedTeam(p);
                    org.bukkit.scoreboard.Team st = g.getScoreBoardTeam(t);
                    g.broadcast(t, st.getColor()+"("+ st.getDisplayName() +") §r" + ANNIUtil.teamPrefixSuffixAppliedName(p) + "§f: " + m);
                    e.setCancelled(true);
                }
            } else {
                e.setFormat("§8(Global)§r %1$s§r: %2$s");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
