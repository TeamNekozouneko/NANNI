package com.nekozouneko.anni.gui.location;

import com.google.gson.Gson;
import com.nekozouneko.anni.ANNIPlugin;
import com.nekozouneko.anni.Team;
import com.nekozouneko.anni.file.ANNIMap;
import com.nekozouneko.anni.util.SimpleLocation;
import org.bukkit.Location;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class NexusLocation implements AbstractGUILocationSelector {

    private final ANNIMap map;
    private final Team team;

    public NexusLocation(ANNIMap m, Team t) {
        this.map = m;
        this.team = t;
    }


    @Override
    public void edit(Location loc) {
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}