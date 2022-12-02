package com.nekozouneko.anni.task;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.Team;
import com.nekozouneko.anni.file.ANNIMap;
import com.nekozouneko.anni.game.ANNIGame;
import com.nekozouneko.anni.game.ANNIStatus;
import com.nekozouneko.anni.game.GameManager;
import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class UpdateBoard extends BukkitRunnable {

    private final Netherboard nb;
    private final ANNIPlugin plugin = ANNIPlugin.getInstance();
    private final GameManager gm = ANNIPlugin.getGM();

    public UpdateBoard(Netherboard nb) {
        this.nb = nb;
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            BPlayerBoard b = nb.getBoard(p);

            TimeZone.setDefault(TimeZone.getTimeZone("GMT+9:00"));
            Calendar cl = Calendar.getInstance(Locale.JAPAN);

            Date d = cl.getTime();

            double bal = ANNIPlugin.getVaultEconomy().getBalance(p);

            if (gm.isJoined(p)) {
                if (b == null) b = nb.createBoard(p, "§cA§9N§eN§aI");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                ANNIGame annig = gm.getPlayerJoinedGame(p);
                ANNIMap m = annig.getMap();
                String mn = (m != null ? m.getDisplay() : "?");

                if (gm.getGame().getStatus() == ANNIStatus.WAITING
                || annig.getStatus() == ANNIStatus.STOPPING || ANNIStatus.CANT_START == annig.getStatus())
                {
                    Team t = annig.getPlayerJoinedTeam(p);
                    String tn = t != null ? annig.getScoreBoardTeam(t).getDisplayName() + "" : "無所属";

                    b.setAll(
                            "§8" + annig.getId16(),
                            "   ",
                            "あなたは" + tn + "です。",
                            "  ",
                            "現在" + annig.getPlayers().size() + "人が参加しています",
                            " ",
                            "§9§nnekozouneko.net"
                    );
                } else {
                    if (gm.getRuleType() == 2) {
                        b.setAll(
                                "§8" + annig.getId16(),
                                "   ",
                                "マップ: §c" + mn,
                                "フェーズ: §c" + annig.getStatus().getPhaseId(),
                                "  ",
                                "§c赤: §7" + annig.getNexusHealth(Team.RED),
                                "§9青: §7" + annig.getNexusHealth(Team.BLUE),
                                " ",
                                "§9§nnekozouneko.net"
                        );
                    } else {
                        b.setAll(
                                "§8" + annig.getId16() + " " + sdf.format(d),
                                "   ",
                                "マップ: §c" + mn,
                                "  ",
                                "§c赤: §7" + annig.getNexusHealth(Team.RED),
                                "§9青: §7" + annig.getNexusHealth(Team.BLUE),
                                "§e黄: §7" + annig.getNexusHealth(Team.YELLOW),
                                "§a緑: §7" + annig.getNexusHealth(Team.GREEN),
                                " ",
                                "§9§nnekozouneko.net"
                        );
                    }
                }
            } else if (p.getWorld() == ANNIPlugin.getLobby().getLocation().getBukkitWorld()) {
                if (b == null) b = nb.createBoard(p, "\u00A7cA\u00A79N\u00A7eN\u00A7aI");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

                b.setAll(
                        "§8" + sdf.format(d),
                        "   ",
                        "お知らせ:",
                        "§7> §f開発中だからバグの森だよ",
                        "  ",
                        "所持ポイント: §c" + bal + " §7" + ANNIPlugin.getVaultEconomy().currencyNameSingular(),
                        " ",
                        "勝利数: §c-1 §8(未実装)",
                        "キル: §c-1 §8(未実装)",
                        "死亡数: §c-1 §8(未実装)",
                        "",
                        "§9§nnekozouneko.net"
                );
            } else {
                if (b != null) b.delete();
            }
        }
    }

    public void stop() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            BPlayerBoard b = nb.getBoard(p);
            if (b == null) continue;

            b.delete();
            cancel();
        }
    }

}
