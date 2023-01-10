package com.nekozouneko.anni.task;

import com.nekozouneko.anni.ANNITeam;
import com.nekozouneko.anni.game.ANNIGame;
import com.nekozouneko.anni.game.ANNIStatus;

import org.bukkit.scheduler.BukkitRunnable;

public class SuddenDeathTask extends BukkitRunnable {

    private final ANNIGame game;

    public SuddenDeathTask(ANNIGame game) {
        this.game = game;
    }

    @Override
    public void run() {
        if (game.getStatus().getPhaseId() == 5) {

            for (ANNITeam t : game.getNotLostTeams()) {
                if (game.getNexusHealth(t) > 1) {
                    game.damageNexusHealth(t, 1);
                }
            }

        }

        if (
                game.getStatus() == ANNIStatus.WAITING_RESTART
                || game.getStatus() == ANNIStatus.WAITING
                || game.getStatus() == ANNIStatus.STOPPING
                || game.getStatus() == ANNIStatus.CANT_START
        ) {
            cancel();
        }
    }
}
