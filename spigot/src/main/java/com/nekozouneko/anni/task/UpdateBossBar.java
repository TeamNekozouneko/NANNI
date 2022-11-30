package com.nekozouneko.anni.task;

import com.nekozouneko.anni.game.ANNIGame;
import com.nekozouneko.anni.game.ANNIStatus;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class UpdateBossBar extends BukkitRunnable {

    private final BossBar bb;
    private final ANNIGame g;
    private static final Map<ANNIStatus, String> message = new HashMap<>(Map.of(
            ANNIStatus.CANT_START, "開始不可 (設定不足)",
            ANNIStatus.PHASE_ONE, "フェーズ 1",
            ANNIStatus.PHASE_TWO, "フェーズ 2",
            ANNIStatus.PHASE_THREE, "フェーズ 3",
            ANNIStatus.PHASE_FOUR, "フェーズ 4",
            ANNIStatus.PHASE_FIVE, "フェーズ 5",
            ANNIStatus.PHASE_SIX, "フェーズ 6",
            ANNIStatus.PHASE_SEVEN, "フェーズ 7",
            ANNIStatus.WAITING, "待機中",
            ANNIStatus.STOPPING, "ゲームを停止中 (運営の操作が必要です)"
    ));

    public UpdateBossBar(ANNIGame g) {
        this.g = g;
        this.bb = g.getBossBar();
    }

    @Override
    public void run() {

    }

}
