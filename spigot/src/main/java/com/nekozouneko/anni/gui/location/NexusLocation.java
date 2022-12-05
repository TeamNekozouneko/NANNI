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

public class NexusLocation implements AbstractGUILocationSelector {

    private final ANNIMap map;
    private final Team team;
    private final Player player;

    public NexusLocation(ANNIMap m, Team t, Player p) {
        this.map = m;
        this.team = t;
        this.player = p;
    }


    @Override
    public void edit(Location loc) {
        try {
            if (loc.getWorld() == Bukkit.getWorld(map.getWorld())) {
                Gson gson = new Gson();
                BufferedWriter bw = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(
                                        new File(ANNIPlugin.getMapDir(), map.getWorld() + ".json")
                                ),
                                StandardCharsets.UTF_8
                        )
                );
                map.setNexus(team, new SimpleLocation(loc));
                gson.toJson(map, ANNIMap.class, bw);

                bw.flush();
                bw.close();

                ANNIPlugin.getMM().unload(map.getWorld());
                ANNIPlugin.getMM().load(new File(ANNIPlugin.getMapDir(), map.getWorld() + ".json"));

                player.sendMessage("ネクサスを設定しました。");
            } else {
                player.sendMessage("マップのワールドと設定するワールドが異なるためキャンセルされました。");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        MapEditor.glc.remove(player.getUniqueId());
    }
}