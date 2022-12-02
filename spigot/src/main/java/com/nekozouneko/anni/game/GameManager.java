package com.nekozouneko.anni.game;

import com.nekozouneko.anni.ANNIPlugin;
import org.bukkit.entity.Player;

public class GameManager {

    private final ANNIGame game;
    private final MapManager mm = ANNIPlugin.getMM();
    private final Integer minPlayers;
    private final Integer maxPlayers;
    private final Integer ruleType;

    public GameManager(Integer min, Integer max, Integer rule) {
        this.minPlayers = min;
        this.maxPlayers = max;
        this.ruleType = rule;
        this.game = new ANNIGame(this);
    }

    public boolean isJoined(Player p) {
        return game.getPlayers().contains(p);
    }

    public boolean join(Player p) {
        if (!isJoined((p))) {
            game.join(p);
            return true;
        }
        return false;
    }

    public void endGame(boolean force) {
        game.end(force);
    }

    public void leaveFromGame(Player p) {
        if (isJoined(p)) {
            game.leave(p);
        }
    }

    public ANNIGame getPlayerJoinedGame(Player p) {
        if (isJoined(p)) {
            if (game.getPlayers().contains(p)) {
                return game;
            }
        }
        return null;
    }

    public Integer getMinPlayers() {
        return minPlayers;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public Integer getRuleType() {
        return ruleType;
    }

    public ANNIGame getGame() {
        return game;
    }

}
