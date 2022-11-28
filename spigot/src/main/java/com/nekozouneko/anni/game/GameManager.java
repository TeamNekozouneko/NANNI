package com.nekozouneko.anni.game;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameManager {

    private final Map<String, ANNIGame> games = new HashMap<>();
    private final Map<UUID, ANNIPlayer> players = new HashMap<>();

    public GameManager() {

    }


    public void leaveFromAllGames(Player p) {
        games.values().forEach((t) -> t.leave(p));
    }

}
