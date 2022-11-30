package com.nekozouneko.anni.task;

import com.nekozouneko.anni.ANNIPlugin;
import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.bukkit.Bukkit;
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

    public UpdateBoard(Netherboard nb) {
        this.nb = nb;

    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            BPlayerBoard b = nb.getBoard(p);

            if (p.getWorld() == ANNIPlugin.getLobby().getLocation().getBukkitWorld()) {
                if (b == null) b = nb.createBoard(p, "\u00A7cA\u00A79N\u00A7eN\u00A7aI");

                TimeZone.setDefault(TimeZone.getTimeZone("GMT+9:00"));
                Calendar cl = Calendar.getInstance(Locale.JAPAN);

                Date d = cl.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

                double bal = ANNIPlugin.getVaultEconomy().getBalance(p);

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
