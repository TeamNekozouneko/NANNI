package com.nekozouneko.anni.task;

import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.ANNIUtil;
import com.nekozouneko.anni.Team;
import com.nekozouneko.anni.game.ANNIBigMessage;
import com.nekozouneko.anni.game.ANNIGame;
import com.nekozouneko.anni.game.ANNIStatus;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UpdateBossBar extends BukkitRunnable {

    private final BossBar bb;
    private final ANNIGame g;
    public static final Map<Team, Character> bigCharMap = Collections.unmodifiableMap(new HashMap<Team, Character>() {{
        put(Team.RED,'R');put(Team.BLUE,'B');put(Team.YELLOW,'Y');put(Team.GREEN,'G');
    }});
    private Map.Entry<ANNIStatus, Integer> timer = new AbstractMap.SimpleEntry<>(ANNIStatus.STOPPING, -1);

    public static final Map<ANNIStatus, String> message = Collections.unmodifiableMap(new HashMap<ANNIStatus, String>() {{
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
    }});
    public static final Map<ANNIStatus, Integer> phase_TIME = Collections.unmodifiableMap(new HashMap<ANNIStatus, Integer>() {{
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
    }});

    public UpdateBossBar(ANNIGame g) {
        this.g = g;
        this.bb = g.getBossBar();

        bb.setColor(BarColor.BLUE);
        bb.removeFlag(BarFlag.CREATE_FOG);
        bb.removeFlag(BarFlag.PLAY_BOSS_MUSIC);
        bb.removeFlag(BarFlag.DARKEN_SKY);
    }

    @Override
    public void run() {
        if (g.getStatus() == ANNIStatus.CANT_START) return;

        bb.removeAll();
        for (Player p : g.getPlayers()) bb.addPlayer(p);

        switch (g.getStatus()) {
            case STOPPING:
                stoppingCase(g.getStatus());
                break;
            case CANT_START:
                cantStartCase(g.getStatus());
                break;
            case WAITING:
                waitingCase(g.getStatus());
                break;
            case PHASE_ONE:
                phase1Case(g.getStatus());
                break;
            case PHASE_TWO:
                phase2Case(g.getStatus());
                break;
            case PHASE_THREE:
                phase3Case(g.getStatus());
                break;
            default: break;
        }
    }

    @Override
    public void cancel() {
        if (!isCancelled()) {
            bb.removeAll();
            super.cancel();
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
        final int ps;
        int psr = g.getPlayers(Team.RED).size();
        int psb = g.getPlayers(Team.BLUE).size();
        int psu = g.getPlayers(Team.NOT_JOINED).size();
        if (g.getManager().getRuleType() == 4) {
            int psg = g.getPlayers(Team.GREEN).size();
            int psy = g.getPlayers(Team.YELLOW).size();

            ps = psr+psb+psg+psy+psu;
        } else ps = psr+psb+psu;

        if (min <= ps) {
            if (g.getTimer() <= -1) {
                g.setTimer(phase_TIME.get(stats));
            }

            if (g.getTimer() == 0) {
                g.start();
                bb.setProgress(ANNIUtil.bossBarProgress(phase_TIME.get(stats), g.getTimer()));
                bb.setTitle(message.get(stats) + " - 開始まであと " + ANNIUtil.toTimerFormat(g.getTimer()));
                g.setTimer(phase_TIME.get(ANNIStatus.PHASE_ONE));
            } else {
                if (g.getTimer() <= 5) {
                    for (Player p : g.getPlayers()) {
                        p.sendTitle(g.getTimer()+"", null, 5, 20, 5);
                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 2);
                    }
                }

                bb.setProgress(ANNIUtil.bossBarProgress(phase_TIME.get(stats), g.getTimer()));
                bb.setTitle(message.get(stats) + " - 開始まであと " + ANNIUtil.toTimerFormat(g.getTimer()));
                g.takeTimer();
            }
        } else {
            if (g.getTimer() >= 0) {
                g.broadcast("誰かが退出したため開始がキャンセルされました。");
                g.setTimer(-1);
            }

            bb.setProgress(ANNIUtil.bossBarProgress(min, ps));
            bb.setTitle(message.get(stats) + " - あと" + (min - ps) + "人必要です。");
        }
    }

    private void phase1Case(final ANNIStatus stats) {
        if (g.getTimer() <= 0) {
            bb.setTitle(message.get(stats) + " - " + ANNIUtil.toTimerFormat(g.getTimer()));
            bb.setProgress(ANNIUtil.bossBarProgress(phase_TIME.get(stats), g.getTimer()));

            g.setTimer(phase_TIME.get(ANNIStatus.PHASE_TWO));
            g.changeStatus(ANNIStatus.PHASE_TWO);
            return;
        }

        bb.setTitle(message.get(stats) + " - " + ANNIUtil.toTimerFormat(g.getTimer()));
        bb.setProgress(ANNIUtil.bossBarProgress(phase_TIME.get(stats), g.getTimer()));
        g.takeTimer();
    }

    private void phase2Case(final ANNIStatus stats) {
        if (g.getTimer() <= 0) {
            bb.setTitle(message.get(stats) + " - " + ANNIUtil.toTimerFormat(g.getTimer()));
            bb.setProgress(ANNIUtil.bossBarProgress(phase_TIME.get(stats), g.getTimer()));

            g.setTimer(phase_TIME.get(ANNIStatus.PHASE_THREE));
            g.changeStatus(ANNIStatus.PHASE_THREE);
            return;
        }

        bb.setTitle(message.get(stats) + " - " + ANNIUtil.toTimerFormat(g.getTimer()));
        bb.setProgress(ANNIUtil.bossBarProgress(phase_TIME.get(stats), g.getTimer()));
        g.takeTimer();
    }

    private void phase3Case(final ANNIStatus stats) {
        if (g.getTimer() <= 0) {
            bb.setTitle(message.get(stats) + " - " + ANNIUtil.toTimerFormat(g.getTimer()));
            bb.setProgress(ANNIUtil.bossBarProgress(phase_TIME.get(stats), g.getTimer()));

            g.setTimer(phase_TIME.get(ANNIStatus.PHASE_FOUR));
            g.changeStatus(ANNIStatus.PHASE_FOUR);
            return;
        }

        bb.setTitle(message.get(stats) + " - " + ANNIUtil.toTimerFormat(g.getTimer()));
        bb.setProgress(ANNIUtil.bossBarProgress(phase_TIME.get(stats), g.getTimer()));
        g.takeTimer();
    }

}
