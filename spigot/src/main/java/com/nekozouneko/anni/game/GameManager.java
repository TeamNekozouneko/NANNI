package com.nekozouneko.anni.game;

import com.nekozouneko.anni.ANNIPlugin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameManager {

    private final Map<String, ANNIGame> games = new HashMap<>();
    private final Map<UUID, ANNIPlayer> players = new HashMap<>();
    private final MapManager mm = ANNIPlugin.getMM();

    public GameManager() {
        mm.getMaps().forEach((k, v) -> games.put(k, new ANNIGame(v)));
    }

    public boolean isJoined(Player p) {
        return players.containsKey(p.getUniqueId());
    }

    public boolean join(String w, Player p) {
        if (!isJoined((p))) {
            if (mm.getMaps().containsKey(w)) {
                ANNIGame g = games.get(w);
                return true;
            }
        }

        return false;
    }

    public void endAllGames() {
        games.values().forEach((g) -> g.end(true));
    }

    public void leaveFromAllGames(Player p) {
        games.values().forEach((t) -> t.leave(p));
    }

}
