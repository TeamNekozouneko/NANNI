package com.nekozouneko.anni.gui.location;

import com.google.gson.Gson;
import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.Team;
import com.nekozouneko.anni.file.ANNIMap;
import com.nekozouneko.anni.gui.MapEditor;
import com.nekozouneko.anni.util.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TeamSpawnPointLocation implements AbstractGUILocationSelector {

    private final Player player;
    private final ANNIMap map;
    private final Team team;

    public TeamSpawnPointLocation(ANNIMap map, Team team, Player player) {
        this.map = map;
        this.team = team;
        this.player = player;
    }

    @Override
    public void edit(Location loc) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(
                                new File(ANNIPlugin.getMapDir(), map.getWorld()+".json")
                        ),
                        StandardCharsets.UTF_8
                )
        )) {
            if (loc.getWorld() == Bukkit.getWorld(map.getWorld())) {
                map.setSpawnPoint(team, new SimpleLocation(loc));

                Gson gson = new Gson();
                gson.toJson(map, ANNIMap.class, writer);

                writer.flush();
                player.sendMessage("スポーン地点を設定しました。");

                ANNIPlugin.getMM().unload(map.getWorld());
                ANNIPlugin.getMM().load(new File(ANNIPlugin.getMapDir(), map.getWorld()+".json"));
            } else {
                player.sendMessage("マップのワールドと設定するワールドが異なるためキャンセルされました。");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        MapEditor.glc.remove(player.getUniqueId());
    }
}
