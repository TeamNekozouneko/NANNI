package com.nekozouneko.anni.task;

import com.nekozouneko.anni.game.ANNIGame;
import com.nekozouneko.anni.game.ANNIStatus;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class UpdateBossBar extends BukkitRunnable {

    private final BossBar bb;
    private final ANNIGame g;
    private Map.Entry<ANNIStatus, Integer> timer = new AbstractMap.SimpleEntry<>(ANNIStatus.STOPPING, -1);

    private static final Map<ANNIStatus, String> message = new HashMap<ANNIStatus, String>() {
        {
            put(ANNIStatus.CANT_START, "開始不可 (設定不足)");
            put(ANNIStatus.PHASE_ONE, "フェーズ 1");
            put(ANNIStatus.PHASE_TWO, "フェーズ 2");
            put(ANNIStatus.PHASE_THREE, "フェーズ 3");
            put(ANNIStatus.PHASE_FOUR, "フェーズ 4");
            put(ANNIStatus.PHASE_FIVE, "フェーズ 5");
            put(ANNIStatus.PHASE_SIX, "フェーズ 6");
            put(ANNIStatus.PHASE_SEVEN, "フェーズ 7");
            put(ANNIStatus.WAITING, "待機中");
            put(ANNIStatus.STOPPING, "ゲームを停止中 (運営の操作が必要です)");
        }
    };
    private static final Map<ANNIStatus, Integer> phase_TIME = new HashMap<ANNIStatus, Integer>() {{
        put(ANNIStatus.CANT_START, -1);
        put(ANNIStatus.PHASE_ONE, 600);
        put(ANNIStatus.PHASE_TWO, 600);
        put(ANNIStatus.PHASE_THREE, 600);
        put(ANNIStatus.PHASE_FOUR, 600);
        put(ANNIStatus.PHASE_FIVE, 600);
        put(ANNIStatus.PHASE_SIX, 600);
        put(ANNIStatus.PHASE_SEVEN, 600);
        put(ANNIStatus.WAITING, 60);
        put(ANNIStatus.STOPPING, -1);
    }};

    public UpdateBossBar(ANNIGame g) {
        this.g = g;
        this.bb = g.getBossBar();
    }

    @Override
    public void run() {
        if (g.getStatus() == ANNIStatus.CANT_START) return;

        switch (g.getStatus()) {
            case STOPPING:
                stoppingCase(g.getStatus());
                break;
            case CANT_START:
                cantStartCase(g.getStatus());
                break;
        }
    }

    private void cantStartCase(ANNIStatus stats) {
        bb.setProgress(1.0);
        bb.setColor(BarColor.RED);
        bb.setTitle(message.get(stats));
    }

    private void stoppingCase(ANNIStatus stats) {
        bb.setProgress(1.0);
        bb.setColor(BarColor.RED);
        bb.setTitle(message.get(stats));
    }

    private void waitingCase(ANNIStatus stats) {
        final int min = g.getManager().getMinPlayers();
        final int max = g.getManager().getMaxPlayers();
        final int ps = g.getPlayers().size();

        if (min <= ps && max >= ps) {

        } else {

        }
    }

}
