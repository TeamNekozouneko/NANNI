package com.nekozouneko.anni.task;

import com.nekozouneko.nutilsxlib.chat.NChatColor;
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

    public UpdateBoard(Netherboard nb) {
        this.nb = nb;

    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            BPlayerBoard b = nb.getBoard(p);
            if (b == null) b = nb.createBoard(p, "\u00A7cAN\u00A79NI");

            TimeZone.setDefault(TimeZone.getTimeZone("GMT+9:00"));
            Calendar cl = Calendar.getInstance(Locale.JAPAN);

            Date d = cl.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

            b.setAll(
                    "§8" + sdf.format(d),
                    " ",
                    "§7状態: §4停止中 (開発中)",
                    "",
                    "§9§nnekozouneko.net"
            );
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
