package com.nekozouneko.anni.task;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.ANNIUtil;
import com.nekozouneko.anni.Team;
import com.nekozouneko.anni.file.ANNIMap;
import com.nekozouneko.anni.game.ANNIGame;
import com.nekozouneko.anni.game.ANNIStatus;
import com.nekozouneko.anni.game.manager.GameManager;
import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.*;

public class UpdateBoard extends BukkitRunnable {

    private final ANNIPlugin plugin = ANNIPlugin.getInstance();
    private final GameManager gm = ANNIPlugin.getGM();
    private final Netherboard nb = ANNIPlugin.getNb();

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(ANNIPlugin.getSb());
            TimeZone.setDefault(TimeZone.getTimeZone(ANNIPlugin.getANNIConf().TimeZone()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            Calendar cl = Calendar.getInstance(Locale.JAPAN);

            Date d = cl.getTime();

            double bal = ANNIPlugin.getVaultEconomy().getBalance(p);

            if (gm.isJoined(p)) {
                BPlayerBoard b = nb.hasBoard(p) ? nb.getBoard(p) : nb.createBoard(p,ANNIPlugin.getSb(),"§cA§9N§eN§aI");
                ANNIGame annig = gm.getPlayerJoinedGame(p);
                ANNIMap m = annig.getMap();
                String mn = (m != null ? m.getDisplay() : "?");

                if (gm.getGame().getStatus() == ANNIStatus.WAITING
                || annig.getStatus() == ANNIStatus.STOPPING || ANNIStatus.CANT_START == annig.getStatus())
                {
                    Team t = annig.getPlayerJoinedTeam(p);
                    org.bukkit.scoreboard.Team st = annig.getScoreBoardTeam(t);
                    String tn = (t != null && st != null) ? st.getColor() + st.getDisplayName() + "" : "無所属";
                    String tm = "";

                    int min = gm.getMinPlayers();
                    int psr = annig.getPlayers(Team.RED).size();
                    int psb = annig.getPlayers(Team.BLUE).size();
                    int psy = annig.getPlayers(Team.YELLOW).size();
                    int psg = annig.getPlayers(Team.GREEN).size();
                    int psn = annig.getPlayers(Team.NOT_JOINED).size();
                    int ps = psr+psb+psg+psy+psn;

                    if (min > ps) {
                        tm = "開始にはあと"+(min-ps)+"人が必要です";
                    } else tm = "開始まであと "+ANNIUtil.toTimerFormat(gm.getGame().getTimer());

                    b.setAll(
                            "§8" + annig.getId16(),
                            "§8" + sdf.format(d),
                            "   ",
                            tm,
                            "  ",
                            "チーム: §7" + tn,
                            "プレイヤー: §7" + annig.getPlayers().size() + " / " + gm.getMaxPlayers(),
                            " ",
                            "§9§nnekozouneko.net"
                    );
                } else {
                    if (gm.getRuleType() == 2) {
                        b.setAll(
                                "§8" + annig.getId16(),
                                "§8" + sdf.format(d),
                                "   ",
                                "マップ: §c" + mn,
                                "フェーズ: §c" + ANNIUtil.phaseId2BoardDisplay(annig.getStatus().getPhaseId()),
                                "  ",
                                "§c赤: §7" + annig.getNexusHealthForBoard(Team.RED),
                                "§9青: §7" + annig.getNexusHealthForBoard(Team.BLUE),
                                " ",
                                "§9§nnekozouneko.net"
                        );
                    } else {
                        b.setAll(
                                "§8" + annig.getId16() + " " + sdf.format(d),
                                "§8" + sdf.format(d),
                                "   ",
                                "マップ: §c" + mn,
                                "  ",
                                "§c赤: §7" + annig.getNexusHealthForBoard(Team.RED),
                                "§9青: §7" + annig.getNexusHealthForBoard(Team.BLUE),
                                "§e黄: §7" + annig.getNexusHealthForBoard(Team.YELLOW),
                                "§a緑: §7" + annig.getNexusHealthForBoard(Team.GREEN),
                                " ",
                                "§9§nnekozouneko.net"
                        );
                    }
                }
            } else if (p.getWorld().equals(ANNIPlugin.getLobby().getLocation().getBukkitWorld())) {
                BPlayerBoard b = nb.hasBoard(p) ? nb.getBoard(p) : nb.createBoard(p,ANNIPlugin.getSb(),"§cA§9N§eN§aI");
                int k = ANNIPlugin.getANNIDB().getKillCount(p.getUniqueId());
                int de = ANNIPlugin.getANNIDB().getDeathCount(p.getUniqueId());
                String late = String.format("%.2f", ANNIUtil.KDCalc(k, de));

                b.setAll(
                        "§8" + sdf.format(d),
                        "    ",
                        "お知らせ:",
                        "§7> §f開発中だからバグの森だよ",
                        "   ",
                        "所持P: §c" + ANNIUtil.doubleToString(bal, true) + " §7" + ANNIPlugin.getVaultEconomy().currencyNameSingular(),
                        "  ",
                        "勝利数: §c" + ANNIPlugin.getANNIDB().getWinCount(p.getUniqueId()),
                        "K/D: §c"+ late +" §8("+k+" / "+de+")",
                        " ",
                        "§9§nnekozouneko.net"
                );
            } else {
                if (nb.hasBoard(p)) nb.removeBoard(p);
            }
        }
    }

    public void stop() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (nb.hasBoard(p)) nb.removeBoard(p);
        }
        cancel();
    }

}
